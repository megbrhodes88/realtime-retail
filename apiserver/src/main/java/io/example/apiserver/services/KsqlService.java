package io.example.apiserver.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.example.apiserver.events.CustomEventPublisher;
import io.confluent.common.config.ConfigException;
import io.confluent.ksql.api.client.BatchedQueryResult;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.StreamedQueryResult;

@Service
public class KsqlService {
    @Autowired
    private CustomEventPublisher publisher;
    // TODO clean this up
    private String configType = (System.getenv("CONFIG_TYPE") != null) ? System.getenv("CONFIG_TYPE") : "FILE"; 
    private String configFile = (System.getenv("CONFIG") != null) ? System.getenv("CONFIG_FILE") : "ksql.properties";
    private Client ksqlClient;

    @EventListener(ApplicationStartedEvent.class)
    private void initClient() throws ConfigException, IOException {
        Properties props = new Properties();
        if (configType.equals("FILE")) {
            addPropsFromFile(props, configFile);
        } else {
            preInitChecks();
            props.put("ksql.url", System.getenv("KSQL_URL"));
            props.put("ksql.port", System.getenv("KSQL_PORT"));
            props.put("ksql.key", System.getenv("KSQL_KEY"));
            props.put("ksql.secret", System.getenv("KSQL_SECRET"));
        }
        ClientOptions ksqlClientOptions = ClientOptions.create()
            .setHost(props.getProperty("ksql.url"))
            .setPort(Integer.valueOf(props.getProperty("ksql.port")))
            .setBasicAuthCredentials(props.getProperty("ksql.key"), props.getProperty("ksql.secret"))
            .setUseTls(true)
            .setUseAlpn(true);
        ksqlClient = Client.create(ksqlClientOptions);
        publisher.publishEvent("KsqlServiceReady"); 
    }

    public List<Row> pullQuery(String query) throws InterruptedException, ExecutionException {
        BatchedQueryResult batchedQueryResult = ksqlClient.executeQuery(query);
        return batchedQueryResult.get();
    }

    public CompletableFuture<StreamedQueryResult> streamQuery(String query) {
        return ksqlClient.streamQuery(query);
    }

    @PreDestroy
    private void closeClient() {
        ksqlClient.close();
    }

    private void preInitChecks() throws ConfigException {
        ArrayList<String> requiredProps = new ArrayList<String>(Arrays.asList("KSQL_URL", "KSQL_KEY", "KSQL_SECRET"));
        ArrayList<String> missingProps = new ArrayList<String>();
        for (String prop : requiredProps) {
            if (System.getenv(prop).equals(null)) {
                missingProps.add(prop);
            }
        }
        if (missingProps.size() > 0) {
            throw new ConfigException("Missing required properties: " + missingProps.toString());
        }
    }

    /**
     * This method should be used to load properties from an application properties file.
     * @param props - An existing Properties object to add the properties to.
     * @param file - An existing file containing properties to add to the Properties object. 
     * @throws IOException
     */
    private static void addPropsFromFile(Properties props, String file) throws IOException {
        if (!Files.exists(Paths.get(file))) {
            throw new IOException("Ksql config file (" + file + ") does not exist or was not found.");
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            props.load(inputStream);
        }
    }
}
