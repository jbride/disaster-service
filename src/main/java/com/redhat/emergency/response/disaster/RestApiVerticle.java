package com.redhat.emergency.response.disaster;

import com.redhat.emergency.response.disaster.cache.CacheAccessVerticle;
import com.redhat.emergency.response.disaster.model.DisasterCenter;
import com.redhat.emergency.response.disaster.model.InclusionZone;
import com.redhat.emergency.response.disaster.model.Shelter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.impl.OAuth2TokenImpl;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;

public class RestApiVerticle extends CacheAccessVerticle {

    private static final Logger log = LoggerFactory.getLogger(RestApiVerticle.class);

    @Override
    protected void init(Future<Void> startFuture) {
        if (! cache.containsKey("disaster")) {
            //default to wilmington
            vertx.fileSystem().readFile("wilmington.json", file -> {
                applyDisasterJson((JsonObject)Json.decodeValue(file.result().toString()));
            });
        }

        Router router = Router.router(vertx);

        router.route("/metrics").handler(PrometheusScrapingHandler.create());
        router.route().handler(BodyHandler.create());

        //configure KeyCloak
        JsonObject keycloakJson = new JsonObject()
            .put("realm", config().getString("REALM"))
            .put("auth-server-url", config().getString("AUTH_URL"))
            .put("ssl-required", "external")
            .put("resource", config().getString("VERTX_CLIENTID"))
            .put("credentials", new JsonObject().put("secret", config().getString("VERTX_CLIENT_SECRET")))
            .put("confidential-port", 0);
        OAuth2Auth oauth2 = KeycloakAuth.create(vertx, OAuth2FlowType.AUTH_CODE, keycloakJson);
        OAuth2AuthHandler oauth2Handler = OAuth2AuthHandler.create(oauth2);
        oauth2Handler.setupCallback(router.get("/callback"));

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
                .register("health", f -> f.complete(Status.OK()));
        router.get("/health").handler(healthCheckHandler);

        //secure endpoints that update the disaster
        router.route("/disaster*").handler(oauth2Handler).handler(this::incidentCommanderHandler);
        router.post("/disaster").handler(this::updateDisaster);
        router.get("/disaster").handler(this::getDisasterDetails);
        router.get("/disaster/defaults/:city").handler(this::defaultDisasterLocation);

        router.get("/shelters").handler(this::getShelters);
        router.get("/inclusion-zones").handler(this::getInclusionZones);
        router.get("/center").handler(this::getDisasterCenter);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(config().getJsonObject("http").getInteger("port", 8080), ar -> {
                if (ar.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(ar.cause());
                }
            });
    }

    /**
     * Return a 403 for this request if the associated SSO user (if applicable) doesn't have the incident_commander role
     * 
     * @param rc the RoutingContext associated with this request
     */
    private void incidentCommanderHandler(RoutingContext rc) {
        OAuth2TokenImpl user = (OAuth2TokenImpl) rc.user();
        user.setTrustJWT(true);
        user.isAuthorized("realm:incident_commander", result -> {
            if ( ! result.result()) {
                log.error("Unauthorized access to resource {} by user {}", rc.request().path(), user.accessToken().getString("preferred_username"));
                rc.response().setStatusCode(403).end();
            } else {
                rc.next();
            }
        });
    }

    private void getDisasterDetails(RoutingContext rc) {
        rc.response().setStatusCode(200).end(new JsonObject(cache.get("disaster")).encodePrettily());
    }

    private void updateDisaster(RoutingContext rc) {
        log.info("Received message: {}", rc.getBodyAsJson().encodePrettily());

        if (applyDisasterJson(rc.getBodyAsJson())) {
            rc.response().setStatusCode(200).end();
        } else {
            rc.response().setStatusCode(400).end();
        }
    }

    /**
     * Update the JSON representation of disaster details in the cache.
     * 
     * @param json the new disaster details 
     * @return true if successful, false otherwise
     */
    private boolean applyDisasterJson(JsonObject json) {
        try {
            json.put("shelters", new JsonArray(Json.encode(Json.decodeValue(json.getJsonArray("shelters").encode(), Shelter[].class))));
            Json.decodeValue(json.getJsonArray("inclusionZones").encode(), InclusionZone[].class);
            Json.decodeValue(json.getJsonObject("center").encode(), DisasterCenter.class);
        } catch (Exception e) {
            log.error("Decoding problem", e);
            return false;
        }

        cache.put("disaster", json.encode());
        return true;
    }

    private void defaultDisasterLocation(RoutingContext rc) {
        String city = rc.request().getParam("city");
        log.info("Received request for default city {}", city);
        if (! vertx.fileSystem().existsBlocking(city + ".json")) {
            rc.response().setStatusCode(404).end("Unrecognized location: " + city);
            return;
        }
        vertx.fileSystem().readFile(city + ".json", file -> {
            applyDisasterJson((JsonObject)Json.decodeValue(file.result().toString()));
            rc.response().setStatusCode(200).sendFile(city + ".json");
        });
    }

    private void getShelters(RoutingContext rc) {
        rc.response().putHeader("Content-type", "application/json").setStatusCode(200).end(new JsonObject(cache.get("disaster")).getJsonArray("shelters").encodePrettily());
    }

    private void getInclusionZones(RoutingContext rc) {
        rc.response().setStatusCode(200).end(new JsonObject(cache.get("disaster")).getJsonArray("inclusionZones").encodePrettily());
    }

    private void getDisasterCenter(RoutingContext rc) {
        rc.response().setStatusCode(200).end(new JsonObject(cache.get("disaster")).getJsonObject("center").encodePrettily());
    }
}
