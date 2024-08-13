package com.skillstorm.configs;

import com.skillstorm.constants.Queues;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${AWS_HOSTNAME:localhost}")
    private String host;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Exchanges:
    @Value("${exchanges.direct}")
    private String directExchange;

    // Set up credentials and connect to RabbitMQ:
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    // Configure the RabbitTemplate:
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setReplyTimeout(6000);
        return rabbitTemplate;
    }

    // Create the exchange:
    @Bean
    public Exchange directExchange() {
        return new DirectExchange(directExchange);
    }

    // Create the queues:

    // From AuthUser-Service:
    @Bean
    public Queue registrationRequestQueue() {
        return new Queue(Queues.REGISTRATION_REQUEST.toString());
    }

    // To AuthUser-Service:
    @Bean
    public Queue registrationResponseQueue() {
        return new Queue(Queues.REGISTRATION_RESPONSE.toString());
    }


    // Bind the queues to the exchange:

    // From AuthUser-Service:
    @Bean
    public Binding registrationRequestBinding(Queue registrationRequestQueue, Exchange directExchange) {
        return BindingBuilder.bind(registrationRequestQueue)
                .to(directExchange)
                .with(Queues.REGISTRATION_REQUEST)
                .noargs();
    }

    // To AuthUser-Service:
    @Bean
    public Binding registrationResponseBinding(Queue registrationResponseQueue, Exchange directExchange) {
        return BindingBuilder.bind(registrationResponseQueue)
                .to(directExchange)
                .with(Queues.REGISTRATION_RESPONSE)
                .noargs();
    }
}
