package net.discordia.kafka.propagate.context.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

@Slf4j
public class KafkaContextUtils {
    public static final String RECORD_CONTEXT_HEADER = "x-request-id";

    public static <U, T> Map<String, String> extractContextHeader(ConsumerRecord<U, T> record) {
        var contextHeader = record.headers().lastHeader(RECORD_CONTEXT_HEADER);
        if (record.headers() != null && contextHeader != null) {
            var value = new String(contextHeader.value());
            var state = new HashMap<String, String>();
            state.put("x-request-id", value);
            return state;
        }

        return Map.of();
    }

    public static <U, T> Map<String, String> parseHeaders(ConsumerRecord<U, T> record) {
        return Arrays.stream(record.headers().toArray())
            .collect(Collectors.toMap(Header::key, h-> new String(h.value())));
    }
}
