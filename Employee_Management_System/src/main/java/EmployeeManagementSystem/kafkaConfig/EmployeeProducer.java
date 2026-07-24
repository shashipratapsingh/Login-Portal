package EmployeeManagementSystem.kafkaConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProducer {

    private static final String TOPIC = "employee-created";

    @Autowired
    private KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    public void sendEmployeeCreatedEvent(EmployeeEvent event) {

        kafkaTemplate.send(TOPIC, event);

        System.out.println("Employee Event Sent : " + event.getEmail());
    }
}