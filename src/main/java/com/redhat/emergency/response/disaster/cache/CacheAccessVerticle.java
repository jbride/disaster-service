package com.redhat.emergency.response.disaster.cache;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public abstract class CacheAccessVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(CacheAccessVerticle.class.getName());

    protected RemoteCacheManager client;
    protected RemoteCache<String, String> cache;

    protected abstract void init(Future<Void> startFuture);

    @Override
    public void start(Future<Void> startFuture) {


        vertx.<RemoteCache<String, String>>executeBlocking(fut -> {

            client = new RemoteCacheManager(getConfigBuilder().build());

            RemoteCache<String, String> cache = client.administration().getOrCreateCache("disaster", "default");
            fut.complete(cache);

        }, res -> {
            if (res.succeeded()) {
                logger.info("Cache connection successfully done");
                this.cache = res.result();
                init(startFuture);
            } else {
                logger.fatal("Cache connection error");
                startFuture.fail(res.cause());
            }
        });
    }


    protected ConfigurationBuilder getConfigBuilder() {
        ConfigurationBuilder cfg = null;
        if (System.getenv("KUBERNETES_NAMESPACE") != null) {

            cfg = ClientConfiguration.create(config().getJsonObject("datagrid").getString("service-name"),
                    config().getJsonObject("datagrid").getString("app-name"),
                    config().getJsonObject("datagrid").getString("user-name"),
                    config().getJsonObject("datagrid").getString("password"));
        } else {
            //local configuration
            cfg = new ConfigurationBuilder().addServer()
                    .host(config().getJsonObject("datagrid").getString("service-name", "localhost"))
                    .port(config().getJsonObject("datagrid").getInteger("port", 11222))
                    .protocolVersion("2.5")
                    .clientIntelligence(ClientIntelligence.BASIC);
        }

        return cfg;

    }

}