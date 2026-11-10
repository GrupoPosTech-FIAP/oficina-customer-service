package com.mecanica.oficina_customer_service.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.application.cliente.usecase.ListarClientesUseCase;
import com.mecanica.oficina_customer_service.domain.cliente.Cliente;
import com.mecanica.oficina_customer_service.domain.cliente.Cpf;
import com.mecanica.oficina_customer_service.domain.cliente.Email;
import com.mecanica.oficina_customer_service.domain.cliente.Telefone;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarClientesUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ListarClientesUseCase useCase;

    @Test
    void deveRetornarListaDeClientes() {
        Cliente cliente = Cliente.reconstituir(
            "id-1",
            "João Silva",
            new Cpf("37518712091"),
            new Email("cliente@teste.com"),
            new Telefone("5437891237"),
            LocalDateTime.now(),
            null
        );
        when(gateway.findAllAtivos()).thenReturn(List.of(cliente));

        List<Cliente> resultado = useCase.executar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveRetornarListaVazia_quandoNaoHaClientes() {
        when(gateway.findAllAtivos()).thenReturn(List.of());

        List<Cliente> resultado = useCase.executar();

        assertThat(resultado).isEmpty();
    }
}
