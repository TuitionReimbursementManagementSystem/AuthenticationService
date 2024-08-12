package com.skillstorm.services;

import com.skillstorm.constants.Role;
import com.skillstorm.dtos.CredentialsDto;
import com.skillstorm.entities.User;
import com.skillstorm.repositories.AuthUserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthUserService implements ReactiveUserDetailsService {

    private final AuthUserRepository authUserRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthUserService(AuthUserRepository authUserRepository, RabbitTemplate rabbitTemplate, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return authUserRepository.findById(username).map(UserDetails::new);
    }

    @RabbitListener(queues = "registration-request-queue")
    public Mono<Void> register(@Payload CredentialsDto credentials, @Header(AmqpHeaders.CORRELATION_ID) String correlationId, @Header(AmqpHeaders.REPLY_TO) String replyToQueue) {

        // Set up User to be saved. Encode password and set default role:
        User newUser = new User();
        newUser.setUsername(credentials.getUsername());
        newUser.setPassword(passwordEncoder.encode(credentials.getPassword()));
        newUser.getRoles().add(Role.ROLE_USER);

        // Save the User and send back a response to indicate completion:
        return authUserRepository.save(newUser)
                .doOnNext(user -> {
                    rabbitTemplate.convertAndSend(replyToQueue, credentials, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                }).then();
    }
}
