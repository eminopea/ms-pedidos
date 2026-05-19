package com.template.ms_pedidos.application.usecase;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.EstadoPedido;
import org.openapitools.model.PedidoRequest;
import org.openapitools.model.ProductoDetalleRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.template.ms_pedidos.infrastructure.adapters.inbound.dto.Cliente;
import com.template.ms_pedidos.domain.model.Pedido;
import com.template.ms_pedidos.domain.model.Producto;
import com.template.ms_pedidos.domain.ports.ClienteRepositoryPort;
import com.template.ms_pedidos.domain.ports.PedidoRepositoryPort;
import com.template.ms_pedidos.domain.ports.ProductoRepositoryPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CrearPedidoUseCaseTest {

    @Mock
    private PedidoRepositoryPort pedidoRepo;

    @Mock
    private ProductoRepositoryPort productoRepo;

    @Mock
    private ClienteRepositoryPort clienteRepo;

    @InjectMocks
    private CrearPedidoUseCase useCase;

    private PedidoRequest request;

    @BeforeEach
    void setUp() {

        ProductoDetalleRequest detalle = new ProductoDetalleRequest();
        detalle.setProductoId("00000");
        detalle.setCantidad(2);
        detalle.setPrecio(new BigDecimal("3000"));

        request = new PedidoRequest();
        request.setDocumentNumber("12345678");
        request.setProductos(List.of(detalle));

    }

    @Test
    void ejecutar_ok() {
        Cliente cliente = new Cliente("PN", "Juan Perez", "juan@email.com", 30, "DNI", "12345678", 1000);

        Producto producto = Producto.builder()
                .id(any())
                .nombre("Producto A")
                .precio(new BigDecimal("10.00"))
                .build();

        when(clienteRepo.obtenerPorDni("12345678"))
                .thenReturn(Mono.just(cliente));

        when(productoRepo.descontarStock(any(), anyInt()))
                .thenReturn(Mono.just(true));

        when(productoRepo.findById(any()))
                .thenReturn(Mono.just(producto));

        when(pedidoRepo.save(any(Pedido.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        Mono<Pedido> result = useCase.ejecutar(request);

        StepVerifier.create(result)
                .assertNext(pedido -> {
                    assertEquals("Juan Perez", pedido.getClient());
                    assertEquals(1, pedido.getProductos().size());
                    assertEquals(new BigDecimal("6000"), pedido.getTotal());
                    assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
                })
                .verifyComplete();

        verify(pedidoRepo).save(any(Pedido.class));
    }

    @Test
    void ejecutar_clienteNoExiste() {
        when(clienteRepo.obtenerPorDni("12345678"))
                .thenReturn(Mono.empty());

        Mono<Pedido> result = useCase.ejecutar(request);

        StepVerifier.create(result)
                .expectErrorMatches(error
                        -> error instanceof ResponseStatusException
                && ((ResponseStatusException) error).getStatusCode() == HttpStatus.NOT_FOUND
                )
                .verify();

        verifyNoInteractions(productoRepo);
    }

    @Test
    void ejecutar_stockInsuficiente() {
        Cliente cliente = new Cliente("PN", "Juan Perez", "juan@email.com", 30, "DNI", "12345678", 1000);

        when(clienteRepo.obtenerPorDni("12345678"))
                .thenReturn(Mono.just(cliente));

        when(productoRepo.descontarStock(any(), anyInt()))
                .thenReturn(Mono.just(false));

        Mono<Pedido> result = useCase.ejecutar(request);

        StepVerifier.create(result)
                .expectErrorMatches(error
                        -> error instanceof ResponseStatusException
                && ((ResponseStatusException) error).getStatusCode() == HttpStatus.CONFLICT
                )
                .verify();

        verify(productoRepo, never()).findById(any());
        verify(pedidoRepo, never()).save(any());
    }

    @Test
    void ejecutar_timeout() {
        Cliente cliente = new Cliente("PN", "Juan Perez", "juan@email.com", 30, "DNI", "12345678", 1000);

        when(clienteRepo.obtenerPorDni("12345678"))
                .thenReturn(Mono.just(cliente));

        when(productoRepo.descontarStock(any(), anyInt()))
                .thenReturn(Mono.delay(Duration.ofSeconds(10)).map(i -> true));

        Mono<Pedido> result = useCase.ejecutar(request);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof TimeoutException)
                .verify();
    }
}
