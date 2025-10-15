package net.discordia.kafka.propagate.context.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.propagation.slf4j.MdcPropagationContext;
import io.micronaut.core.annotation.Blocking;
import io.micronaut.core.async.propagation.ReactorPropagation;
import io.micronaut.core.propagation.PropagatedContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import reactor.core.publisher.Mono;
import static net.discordia.kafka.propagate.context.kafka.KafkaContextUtils.parseHeaders;

@Slf4j
@KafkaListener
@RequiredArgsConstructor
public class MainConsumer {
    private final SecondaryProducer secondaryProducer;

    @Blocking
    @Topic("main-topic")
    public void consume(
        final ConsumerRecord<UUID, MainMessage> record
    ) {
        log.info("Consuming record: {}, with headers: {}", record.value(), parseHeaders(record));
        var headers = KafkaContextUtils.extractContextHeader(record);
        var propagatedContext = PropagatedContext.getOrEmpty()
            .plus(new RequestIdContextElement(headers.get("x-request-id")));

        try(var ignore = propagatedContext.propagate()) {
            Mono.just(record)
                .map(ConsumerRecord::value)
                .flatMap(this::sendSecondaryMessage)
                .block();
        }
    }

    private Mono<Void> sendSecondaryMessage(MainMessage mainMessage) {
        var message = new SecondaryMessage(mainMessage.name());
        var requestId = PropagatedContext.get().find(RequestIdContextElement.class)
            .get().requestId();
        return secondaryProducer.sendSecondaryKafkaMessage(UUID.randomUUID(), message, requestId)
            .then();
    }
}
