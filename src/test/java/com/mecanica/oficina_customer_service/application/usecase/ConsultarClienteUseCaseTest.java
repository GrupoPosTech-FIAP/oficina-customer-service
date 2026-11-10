package com.mecanica.oficina_customer_service.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.application.cliente.usecase.ConsultarClienteUseCase;
import com.mecanica.oficina_customer_service.domain.cliente.Cliente;
import com.mecanica.oficina_customer_service.domain.cliente.Cpf;
import com.mecanica.oficina_customer_service.domain.cliente.Email;
import com.mecanica.oficina_customer_service.domain.cliente.Telefone;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarClienteUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ConsultarClienteUseCase useCase;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        clienteExistente = Cliente.reconstituir(
            "id-1",
            "João Silva",
            new Cpf("37518712091"),
            new Email("cliente@teste.com"),
            new Telefone("5437891237"),
            LocalDateTime.now(),
            null
        );
    }

    @Test
    void deveRetornarCliente_quandoEncontrado() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.of(clienteExistente));

        Optional<Cliente> resultado = useCase.executar("37518712091");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveRetornarVazio_quandoClienteNaoEncontrado() {
        when(gateway.findByDocumentoAtivo("37518712091")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = useCase.executar("37518712091");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveLancarExcecao_quandoDocumentoInvalido() {
        assertThatThrownBy(() -> useCase.executar("123"))
            .isInstanceOf(IllegalArgumentException.class);

        verify(gateway, never()).findByDocumentoAtivo(anyString());
    }
}
