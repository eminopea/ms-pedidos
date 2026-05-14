package com.template.ms_pedidos.application.usecase;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.template.ms_pedidos.domain.model.Pedido;
import com.template.ms_pedidos.domain.ports.PedidoRepositoryPort;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ConsultarPedidosUseCaseTest {

    @Mock
    private PedidoRepositoryPort repo;

    @InjectMocks
    private ConsultarPedidosUseCase useCase;

    @Test
    void execute_ok() {

        Pedido p1 = new Pedido();
        Pedido p2 = new Pedido();

        when(repo.findAllOrderByFecha()).thenReturn(Flux.just(p1, p2));

        StepVerifier.create(useCase.execute())
                .expectNext(p1)
                .expectNext(p2)
                .verifyComplete();

        verify(repo).findAllOrderByFecha();
    }

    @Test
    void execute_timeout() {

        when(repo.findAllOrderByFecha())
                .thenReturn(Flux.never());

        StepVerifier.create(useCase.execute())
                .expectError(TimeoutException.class)
                .verify();
    }
}
