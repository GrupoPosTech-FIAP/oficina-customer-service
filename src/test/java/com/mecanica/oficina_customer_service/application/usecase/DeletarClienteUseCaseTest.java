package com.mecanica.oficina_customer_service.application.usecase;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.application.cliente.usecase.DeletarClienteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletarClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private DeletarClienteUseCase useCase;

    @Test
    void deveDeletarClienteComSucesso() {
        assertThatNoException().isThrownBy(() -> useCase.executar("37518712091"));

        verify(gateway).softDelete("37518712091");
    }

    @Test
    void deveDeletarClienteNormalizandoDocumentoFormatado() {
        useCase.executar("375.187.120-91");

        verify(gateway).softDelete("37518712091");
    }

    @Test
    void deveLancarExcecao_quandoDocumentoInvalido() {
        assertThatThrownBy(() -> useCase.executar("123"))
            .isInstanceOf(IllegalArgumentException.class);

        verify(gateway, never()).softDelete(anyString());
    }
}
