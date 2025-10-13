package net.discordia.kafka.propagate.context.kafka;

import io.micronaut.core.propagation.PropagatedContextElement;

public record RequestIdContextElement(String requestId) implements PropagatedContextElement { }