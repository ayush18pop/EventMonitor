package org.example.controller;

import io.prometheus.client.Counter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class ConsumerController {

    private static final Counter startLearningCounter = Counter.build()
            .name("start_learning_counter")
            .help("Counter for start learning events")
            .register();

    private static final Counter exploreCoursesCounter = Counter.build()
            .name("explore_courses_counter")
            .help("Counter for explore courses events")
            .register();


    @KafkaListener(topics = "tracking-events", groupId = "consumer-group")
    public void consume(String eventData) {
        System.out.println("Consumed event: " + eventData);
        if(eventData.contains("userClick")) {
            startLearningCounter.inc();
            System.out.println("Incremented start learning counter");
        } else if(eventData.contains("exploreClick")) {
            exploreCoursesCounter.inc();
            System.out.println("Incremented explore courses counter");
        } else {
            System.out.println("No matching event type found for: " + eventData);
        }

    }
}
