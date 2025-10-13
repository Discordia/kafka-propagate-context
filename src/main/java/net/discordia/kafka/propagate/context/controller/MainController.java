package net.discordia.kafka.propagate.context.controller;

import io.micronaut.context.propagation.slf4j.MdcPropagationContext;
import io.micronaut.core.propagation.PropagatedContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.discordia.kafka.propagate.context.kafka.MainMessage;
import net.discordia.kafka.propagate.context.kafka.MainProducer;
import reactor.core.publisher.Mono;
import static io.micronaut.http.HttpStatus.ACCEPTED;

@Slf4j
@Controller("/main")
@RequiredArgsConstructor
public class MainController {
    private final MainProducer mainProducer;

    @Post("/")
    @Status(ACCEPTED)
    public Mono<Void> handleMain(@Body MainRequest request, HttpRequest<?> httpRequest) {
        log.info("Received request: {}, with headers: {}", request, parseHeaders(httpRequest));
        return Mono.just(request)
            .flatMap(this::sendMainKafkaMessage);
    }

    private Mono<Void> sendMainKafkaMessage(MainRequest mainRequest) {
        var message = new MainMessage(mainRequest.name());
        var requestId = PropagatedContext.get().find(MdcPropagationContext.class)
            .get().state()
            .get("x-request-id");

        return mainProducer.sendMainKafkaMessage(UUID.randomUUID(), message, requestId)
            .then();
    }

    private static Map<String, List<String>> parseHeaders(final HttpRequest<?> httpRequest) {
        return httpRequest.getHeaders().asMap();
    }
}
