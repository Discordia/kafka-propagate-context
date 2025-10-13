package net.discordia.kafka.propagate.context.controller;

import io.micronaut.context.propagation.slf4j.MdcPropagationContext;
import io.micronaut.core.propagation.MutablePropagatedContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.annotation.ServerFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import static io.micronaut.http.HttpHeaders.CONNECTION;
import static io.micronaut.http.HttpHeaders.CONTENT_LENGTH;
import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;
import static io.micronaut.http.annotation.Filter.MATCH_ALL_PATTERN;

@ServerFilter(MATCH_ALL_PATTERN)
@AllArgsConstructor
public class HttpRequestServerFilter {

    public static final List<String> FILTERED_HEADERS = List.of(
        CONTENT_TYPE,
        CONTENT_LENGTH,
        CONNECTION
    );

    @RequestFilter
    public void requestContextFilter(HttpRequest<?> request, MutablePropagatedContext mutablePropagatedContext) {
        try {
            final Map<String, String> contextFromRequestHeaders = getContextFromRequestHeaders(request);
            final MdcPropagationContext mdcPropagationContext = new MdcPropagationContext(contextFromRequestHeaders);
            mutablePropagatedContext.add(mdcPropagationContext);
            contextFromRequestHeaders.forEach(MDC::put);
        } finally {
            MDC.clear();
        }
    }

    private Map<String, String> getContextFromRequestHeaders(HttpRequest<?> request) {
        final HashMap<String, String> context = new HashMap<>();
        // Copy over all headers, filter out FILTERED_HEADERS
        request.getHeaders().asMap().forEach((key, value) -> {
            value.stream().findFirst()
                .filter(val -> !val.isEmpty())
                .filter(val -> !shouldFilterContext(key))
                .ifPresent(val -> context.put(key, val));
        });

        return context;
    }

    // Remove http specific headers from context
    private boolean shouldFilterContext(String key) {
        return FILTERED_HEADERS.stream().anyMatch(filteredHeader -> filteredHeader.equalsIgnoreCase(key));
    }
}
