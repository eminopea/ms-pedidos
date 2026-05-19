package com.template.ms_pedidos.infrastructure.adapters.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.ms_pedidos.domain.events.PedidoAprobadoEvent;
import com.template.ms_pedidos.domain.ports.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.EstadoPedido;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoAprobadoConsumer {

    private final PedidoRepositoryPort pedidoRepo;

    private final ObjectMapper mapper;

    @KafkaListener(
            topics = "${topics.order-approved}",
            groupId = "pedido-group"
    )
    public void consumir(String message) {

        try {

            PedidoAprobadoEvent event =
                    mapper.readValue(
                            message,
                            PedidoAprobadoEvent.class
                    );

            pedidoRepo.findById(event.getPedidoId())

                    .flatMap(pedido -> {

                        pedido.setEstado(
                                EstadoPedido.APROBADO
                        );

                        return pedidoRepo.save(pedido);
                    })

                    .subscribe();

        } catch (Exception e) {

            log.error(e.getMessage());
        }
    }
}
