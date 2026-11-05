package com.mecanica.oficina_customer_service.application.cliente.usecase;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.domain.cliente.Cliente;
import com.mecanica.oficina_customer_service.domain.cliente.Documento;
import java.util.Optional;

public class ConsultarClienteUseCase {
    
    private final ClienteGateway gateway;

    public ConsultarClienteUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public Optional<Cliente> executar(String documento) {
        Documento doc = Documento.parse(documento);
        return gateway.findByDocumentoAtivo(doc.getValue());
    }
}
