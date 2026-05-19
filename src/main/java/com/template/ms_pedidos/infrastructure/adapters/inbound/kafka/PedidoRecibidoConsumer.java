package com.template.ms_pedidos.infrastructure.adapters.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.ms_pedidos.domain.events.PedidoRecibidoEvent;
import com.template.ms_pedidos.domain.ports.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.EstadoPedido;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoRecibidoConsumer {

    private final PedidoRepositoryPort pedidoRepo;

    private final ObjectMapper mapper;

    @KafkaListener(
            topics = "${topics.order-received}",
            groupId = "pedido-group"
    )
    public void consumir(String message) {

        try {

            PedidoRecibidoEvent event =
                    mapper.readValue(
                            message,
                            PedidoRecibidoEvent.class
                    );

            pedidoRepo.findById(event.getPedidoId())

                    .flatMap(pedido -> {

                        pedido.setEstado(
                                EstadoPedido.RECIBIDO
                        );

                        return pedidoRepo.save(pedido);
                    })

                    .subscribe();

        } catch (Exception e) {

            log.error(e.getMessage());
        }
    }
}
