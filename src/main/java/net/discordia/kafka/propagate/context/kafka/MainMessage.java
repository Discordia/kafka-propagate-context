package net.discordia.kafka.propagate.context.kafka;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record MainMessage(String name) {
}
