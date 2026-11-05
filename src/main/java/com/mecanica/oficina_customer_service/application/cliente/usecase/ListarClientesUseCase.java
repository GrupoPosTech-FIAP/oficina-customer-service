package com.mecanica.oficina_customer_service.application.cliente.usecase;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.domain.cliente.Cliente;
import java.util.List;

public class ListarClientesUseCase {
    
    private final ClienteGateway gateway;

    public ListarClientesUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public List<Cliente> executar() {
        return gateway.findAllAtivos();
    }

}
