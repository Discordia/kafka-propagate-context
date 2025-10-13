package net.discordia.kafka.propagate.context.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageBody;
import io.micronaut.messaging.annotation.MessageHeader;
import java.util.UUID;
import reactor.core.publisher.Mono;

@KafkaClient
public interface MainProducer {

    @Topic("main-topic")
    Mono<MainMessage> sendMainKafkaMessage(
        @KafkaKey UUID key,
        @MessageBody MainMessage message,
        @MessageHeader("x-request-id") String header);
}
