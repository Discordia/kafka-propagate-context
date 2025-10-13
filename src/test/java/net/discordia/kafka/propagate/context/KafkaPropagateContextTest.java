package net.discordia.kafka.propagate.context;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import net.discordia.kafka.propagate.context.kafka.SecondaryMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest(transactional = false)
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaPropagateContextTest implements TestPropertyProvider {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    HttpClient client;

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.9.1"));

    @Override
    public @NonNull Map<String, String> getProperties() {
        if (!kafka.isRunning()) {
            kafka.start();
        }
        return Collections.singletonMap("kafka.bootstrap.servers", kafka.getBootstrapServers());
    }

    @Test
    void testPropagateRequestId() {
        // The request id to propagate through all steps
        final String requestId = UUID.randomUUID().toString();

        var secondaryConsumer = application.getApplicationContext().getBean(SecondaryConsumer.class);

        // Send http
        var request = HttpRequest.POST("/main", "{\"name\": \"pelle\"}").header("x-request-id", requestId);
        var response = client.toBlocking().exchange(request, Void.class);
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.ACCEPTED.getCode());

        // Assert consumer got message with header
        Awaitility.await().untilAsserted(() -> {
            var records = secondaryConsumer.getRecords();
            assertThat(records).isNotEmpty();

            var record = records.getFirst();
            var headerValue = record.headers().lastHeader("x-request-id");
            assertThat(headerValue).isNotNull();
            assertThat(new String(headerValue.value())).isEqualTo(requestId);
        });
    }
}
