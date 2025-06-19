package org.example.controller;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    private static final String TOPIC_NAME = "tracking-events";
    private static final String BOOTSTRAP_SERVERS = "192.168.0.108:9092"; // <- Replace with your actual IP

    private final Producer<String, String> kafkaProducer;

    public ProducerController() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.kafkaProducer = new KafkaProducer<>(props);
    }

    @PostMapping("/event")
    public String sendEventToKafka(@RequestBody Map<String, String> payload) {
        String eventType = payload.get("event");
        if (eventType == null || eventType.isEmpty()) {
            return "Missing 'event' in request body.";
        }

        kafkaProducer.send(new ProducerRecord<>(TOPIC_NAME, "userEvent", eventType), (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending message to Kafka: " + exception.getMessage());
            } else {
                System.out.println("Event sent to Kafka topic '" + TOPIC_NAME + "' at offset: " + metadata.offset());
            }
        });

        return "Sent event: `" + eventType + "` to topic `" + TOPIC_NAME + "`";
    }
}
