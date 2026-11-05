package com.mecanica.oficina_customer_service.adapters.web.presenter;

import com.mecanica.oficina_customer_service.adapters.web.dto.response.ConsultarClienteResponse;
import com.mecanica.oficina_customer_service.domain.cliente.Cliente;
import java.util.List;
import org.springframework.stereotype.Component;

/** Converte a entidade de domínio {@link Cliente} em DTOs de resposta HTTP. */
@Component
public class ClientePresenter {

    public ConsultarClienteResponse apresentar(Cliente c) {
        return new ConsultarClienteResponse(
                c.getId(), c.getNome(), c.getDocumento().getValue(),
                c.getEmail().getValue(), c.getTelefone().getValue());
    }

    public List<ConsultarClienteResponse> apresentar(List<Cliente> clientes) {
        return clientes.stream().map(this::apresentar).toList();
    }
}
