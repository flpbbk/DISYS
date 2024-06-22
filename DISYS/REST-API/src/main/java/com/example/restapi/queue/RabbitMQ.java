package com.example.restapi.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQ {

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private int port;

    public void send(String queueName, String message) {
        if (queueName == null || queueName.trim().isEmpty()) {
            throw new IllegalArgumentException("Queue name cannot be null or empty.");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        // Creates and configures a new connection to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);

        try (Connection connection = factory.newConnection(); // neue Verbindung
             Channel channel = connection.createChannel()) {  // neuen Kanal innerhalb der Verbindung

            // Declares the queue (creates it if it doesn't exist)
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));

            // Output for successful message sending
            System.out.println("[REST API] Sent: '" + message + "' to queue: '" + queueName + "'");
        } catch (IOException | TimeoutException e) {
            // Error handling for connection and communication failures
            System.err.println("[REST API] Error sending message: " + e.getMessage());
        }
    }
}
