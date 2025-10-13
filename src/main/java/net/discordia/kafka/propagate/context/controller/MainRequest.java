package net.discordia.kafka.propagate.context.controller;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record MainRequest(
    String name
) {
}
