package com.mecanica.oficina_customer_service.application.cliente.usecase;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.domain.cliente.Documento;

public class DeletarClienteUseCase {
    private final ClienteGateway gateway;

    public DeletarClienteUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public void executar(String documento) {
        Documento doc = Documento.parse(documento);
        gateway.softDelete(doc.getValue());
    }

}
