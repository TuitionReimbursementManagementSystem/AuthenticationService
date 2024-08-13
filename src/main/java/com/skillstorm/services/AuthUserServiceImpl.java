package com.skillstorm.services;

import com.skillstorm.constants.Role;
import com.skillstorm.dtos.AuthUserDto;
import com.skillstorm.dtos.CredentialsDto;
import com.skillstorm.entities.AuthUser;
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
public class AuthUserServiceImpl implements AuthUserService, ReactiveUserDetailsService {

    private final AuthUserRepository authUserRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthUserServiceImpl(AuthUserRepository authUserRepository, RabbitTemplate rabbitTemplate, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    // Find User by Username. Needed to load User into SecurityContext for Spring Security:
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return null;
    }

    // Users register with the User-Service, but username and password get sent here to store for authentication and authorization:
    @RabbitListener(queues = "registration-request-queue")
    public Mono<Void> register(@Payload CredentialsDto credentials, @Header(AmqpHeaders.CORRELATION_ID) String correlationId, @Header(AmqpHeaders.REPLY_TO) String replyToQueue) {

        // Set up AuthUser to be saved. Encode password and set default role:
        AuthUser newAuthUser = new AuthUser();
        newAuthUser.setUsername(credentials.getUsername());
        newAuthUser.setPassword(passwordEncoder.encode(credentials.getPassword()));
        newAuthUser.getRoles().add(Role.ROLE_USER);

        // Save the AuthUser and send back a response to indicate completion:
        return authUserRepository.save(newAuthUser)
                .doOnNext(authUser ->
                    rabbitTemplate.convertAndSend(replyToQueue, credentials, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    })
                ).then();
    }

    // Login:
    @Override
    public Mono<AuthUserDto> login(CredentialsDto credentials) {
        return null;
    }
}
