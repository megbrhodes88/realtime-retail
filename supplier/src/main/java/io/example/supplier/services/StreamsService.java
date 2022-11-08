package io.example.supplier.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.confluent.common.config.ConfigException;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializerConfig;
import io.confluent.kafka.streams.serdes.json.KafkaJsonSchemaSerde;
import io.example.supplier.models.LowInventoryAlert;
import io.example.supplier.models.Shipment;
import io.example.supplier.models.Shipment.ShipmentItem;

@Service
public class StreamsService {
    private static final String configType = (System.getenv("CONFIG_TYPE") != null) ? System.getenv("CONFIG_TYPE") : "FILE"; 
    private static final String configFile = (System.getenv("CLIENT_CONFIG_FILE") != null) ? System.getenv("CLIENT_CONFIG_FILE") : "setup.properties";
    private static final String SOURCE_TOPIC = "low_inventory";
    private static final String SINK_TOPIC = "shipments";

    @EventListener(ApplicationStartedEvent.class) 
    private void initSteams() throws ConfigException, IOException {
        final Properties streamsConfig = new Properties();
        if (configType.equals("FILE")) {
            addPropsFromFile(streamsConfig, configFile);
        } else {
            preInitChecks();
            streamsConfig.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, System.getenv("BOOTSTRAP_SERVERS"));
            streamsConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            streamsConfig.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule   required username='"+System.getenv("KAFKA_KEY")+"'   password='"+System.getenv("KAFKA_SECRET")+"';");
            streamsConfig.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            streamsConfig.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv("SCHEMA_REGISTRY_URL"));
            streamsConfig.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
            streamsConfig.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, System.getenv("SCHEMA_REGISTRY_KEY")+":"+System.getenv("SCHEMA_REGISTRY_SECRET"));
        }
        
        streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, "supplier-service");
        streamsConfig.put(StreamsConfig.CLIENT_ID_CONFIG, "supplier-service-client");
        streamsConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaJsonSchemaSerde.class);
        streamsConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        final KafkaStreams streams = buildStream(streamsConfig);
        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
         Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
    private static KafkaJsonSchemaSerde<LowInventoryAlert> createLowInventoryAlertSerde(Properties streamsConfig) {
        KafkaJsonSchemaSerde<LowInventoryAlert> lowInvAlertSerde = new KafkaJsonSchemaSerde<>();
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put(KafkaJsonSchemaDeserializerConfig.JSON_VALUE_TYPE, LowInventoryAlert.class.getName());
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, streamsConfig.getProperty("schema.registry.url"));
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, streamsConfig.getProperty("basic.auth.credentials.source"));
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, streamsConfig.getProperty("basic.auth.user.info"));
        lowInvAlertSerde.configure(serdeConfig, false);
        return lowInvAlertSerde;
    }
    private static KafkaJsonSchemaSerde<Shipment> createShipmentSerde(Properties streamsConfig) {
        KafkaJsonSchemaSerde<Shipment> shipmentSerde = new KafkaJsonSchemaSerde<>();
        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, streamsConfig.getProperty("schema.registry.url"));
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, streamsConfig.getProperty("basic.auth.credentials.source"));
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, streamsConfig.getProperty("basic.auth.user.info"));
        shipmentSerde.configure(serdeConfig, false);
        return shipmentSerde;
    }
    private KafkaStreams buildStream(Properties streamsConfig) {
        final StreamsBuilder builder = new StreamsBuilder();
        KafkaJsonSchemaSerde<LowInventoryAlert> lowInvAlertSerde = createLowInventoryAlertSerde(streamsConfig);
        final KStream<String, LowInventoryAlert> lowInventoryStream = builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), lowInvAlertSerde));
        KafkaJsonSchemaSerde<Shipment> shipmentSerde = createShipmentSerde(streamsConfig);
        KStream <String, Shipment> shipmentsStream = lowInventoryStream.map(new KeyValueMapper<String, LowInventoryAlert, KeyValue<String, Shipment>>() {
            public KeyValue<String, Shipment> apply(String key, LowInventoryAlert value) {
                System.out.println("Key: "+key+", Value: "+value);
                Shipment shipment = new Shipment();
                shipment.setShipmentId(UUID.randomUUID().toString());
                ShipmentItem shipmentItem = new ShipmentItem();
                shipmentItem.setProductId(key);
                shipmentItem.setShipmentQuantity(100-value.getAvailableInv());
                List<ShipmentItem> shipmentItems = new ArrayList<ShipmentItem>();
                shipmentItems.add(shipmentItem);
                shipment.setShipmentItems(shipmentItems);
                return new KeyValue<>(UUID.randomUUID().toString(), shipment);
            }
        });
        shipmentsStream.to(SINK_TOPIC, Produced.with(Serdes.String(), shipmentSerde));
        return new KafkaStreams(builder.build(), streamsConfig);
    }
    private void preInitChecks() throws ConfigException {
        ArrayList<String> requiredProps = new ArrayList<String>(Arrays.asList("BOOTSTRAP_SERVERS", "KAFKA_KEY", "KAFKA_SECRET", "SCHEMA_REGISTRY_URL", "SCHEMA_REGISTRY_KEY", "SCHEMA_REGISTRY_SECRET"));
        ArrayList<String> missingProps = new ArrayList<String>();
        for (String prop : requiredProps) {
            if (System.getenv(prop) == null) {
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
            throw new IOException("Producer config file (" + file + ") does not exist or was not found.");
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            props.load(inputStream);
        }
    }
}
