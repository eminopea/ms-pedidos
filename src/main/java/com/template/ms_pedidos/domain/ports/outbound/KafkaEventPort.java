package com.template.ms_pedidos.domain.ports.outbound;

import reactor.core.publisher.Mono;

public interface KafkaEventPort {
    Mono<Void> publish(String topic, Object event);
}
