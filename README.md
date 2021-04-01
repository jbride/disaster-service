Disaster Service

# 1. Overview
This service maintains the following information:
* **Disaster Center:** the coordinates and city name (if applicable) of the center of the disaster
* **Shelters:** the name and coordinates of Shelters in the disaster
* **Inclusion Zones:** coordinates for the vertices of geographic polygons in which incidents should spawn
<br/>
Implemented with Vert.x, version 3.6.3.redhat-00009 (latest RHOAR 04/19)
Uses reactive programming with rx-java


# 2. Add Pre-Set Disaster Locations
ER-Demo includes disaster locations for the following areas:

1. Wilmington, North Carolina, United States  (default)
2. New York City, New York, United States
3. Rio De Janeiro, Brazil
4. London, England, United Kingdom
5. Singapore

This section of documentation describes how to add more disaster location options to the demo.

## 2.1. Draw new Disaster Location

Log into your Emergency Response demo environment as incident commander.

Draw your new Disaster Location and shelters.

Save your changes.

## 2.2. Retrieve Disaster Location JSON

The JSON representation of the disaster location is needed.  This JSON representation can be retrieved by invoking the _disaster-service_.  The _disaster-service_ is secured via RH-SSO so you'll need to retrieve an SSO token.

1. Create a route from the _disaster-service_ service
   ```
   $ oc create route edge --service=disaster-service
   ```

2. Set an environment variable that captures the domain of your OpenShift cluster:
   ```
   $ SUBDOMAIN_BASE=cluster-1234.1234.example.opentlc.com
   ```

3. Set environment variables needed to retrieve an access token from RH-SSO:
   ```
   $ retrieve_token_url="https://sso-er-sso.apps.$SUBDOMAIN_BASE/auth/realms/user1-emergency-realm/protocol/openid-connect/token"

   $ sso_vertx_client_secret=  ( found here: https://github.com/Emergency-Response-Demo/erdemo-operator/blob/main/playbooks/group_vars/sso_realm.yml#L7 )

   $ sso_incident_commander_password=  ( found here: https://github.com/Emergency-Response-Demo/erdemo-operator/blob/main/playbooks/group_vars/sso_realm.yml#L5 )
   ```

4.  Retrieve an access token from RH-SSO:
    ```
    TKN=$(curl -k -X POST "$retrieve_token_url" \
            -H "Content-Type: application/x-www-form-urlencoded" \
            -d "username=incident_commander" \
            -d "client_secret=$sso_vertx_client_secret" \
            -d "password=$sso_incident_commander_password" \
            -d "grant_type=password" \
            -d "client_id=vertx" \
            | sed 's/.*access_token":"//g' | sed 's/".*//g')
     ```

5.  Using the SSO access token, invoke the disaster service to retrieve the json representation of the current disaster location:
    ```
    curl -k -v -X GET \
       -H "Authorization: Bearer $TKN" \
       https://disaster-service-user1-er-demo.apps.$SUBDOMAIN_BASE/disaster \
       > /tmp/hong-kong.json
    ```

## 2.3. Include Disaster Location in future versions of ER-Demo

1. Save the response body in a JSON file (ie: hong-kong.json from the previous command) in the [resources directory](https://github.com/Emergency-Response-Demo/disaster-service/tree/master/src/main/resources) of this project.
   
2. Add the new option to the _disaster location_ drop-down of the [Emergency Console source code](https://github.com/andykrohg/emergency-console/blob/disaster-location/src/app/disaster-location/disaster-location.component.html#L11)
3. Build ER-Demo from source and test the new disaster locations
4. Coordinate with the ER-Demo release manager to push your code changes to the ER-Demo master branches to have included in the next release of the demo