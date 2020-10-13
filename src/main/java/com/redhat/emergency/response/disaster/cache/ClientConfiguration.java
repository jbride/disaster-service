package com.redhat.emergency.response.disaster.cache;

import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

class ClientConfiguration {

    private static final String CRT_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt";


    private ClientConfiguration() {
    }

    static ConfigurationBuilder create(String svcName, String saslName, String user, String password) {

        final ConfigurationBuilder cfg = new ConfigurationBuilder();

        cfg
                .addServer()
                .host(svcName)
                .port(11222)
                .security()
//                  .ssl()
//                    .trustStorePath(CRT_PATH)
                  .authentication()
                    .enable()
                    .username(user)
                    .password(password)
                    .realm("default")
                    .serverName(saslName)
                    .saslMechanism("PLAIN");
            

        return cfg;
    }

}
