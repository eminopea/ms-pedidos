package com.template.ms_pedidos.infrastructure.adapters.outbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.ms_pedidos.domain.ports.outbound.KafkaEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KafkaProducerAdapter implements KafkaEventPort {

    private final ReactiveKafkaProducerTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publish(String topic, Object event) {

        return Mono.fromCallable(() ->
                        objectMapper.writeValueAsString(event)
                )
                .flatMap(json ->
                        kafkaTemplate.send(topic, json)
                )
                .then();
    }
}
