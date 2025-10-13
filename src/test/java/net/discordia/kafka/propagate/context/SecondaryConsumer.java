package net.discordia.kafka.propagate.context;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.core.annotation.Blocking;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.discordia.kafka.propagate.context.kafka.SecondaryMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@KafkaListener
public class SecondaryConsumer {
    private final List<ConsumerRecord<UUID, SecondaryMessage>> records = new ArrayList<>();

    @Blocking
    @Topic("secondary-topic")
    public void consume(
        final ConsumerRecord<UUID, SecondaryMessage> record
    ) {
        records.add(record);
    }

    public List<ConsumerRecord<UUID, SecondaryMessage>> getRecords() {
        return records;
    }
}
