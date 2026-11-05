package com.mecanica.oficina_customer_service.application.cliente.usecase;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.domain.cliente.Cliente;
import com.mecanica.oficina_customer_service.domain.cliente.Documento;
import com.mecanica.oficina_customer_service.domain.cliente.Email;
import com.mecanica.oficina_customer_service.domain.cliente.Telefone;

public class AlterarClienteUseCase {
        private final ClienteGateway gateway;

    public AlterarClienteUseCase(ClienteGateway gateway) {
        this.gateway = gateway;
    }

    public void executar(String documento, String nome, String email, String telefone) {
        Documento doc = Documento.parse(documento);
        Cliente existente = gateway.buscarPorDocumentoOuFalhar(doc.getValue());
        existente.alterar(nome, doc, new Email(email), new Telefone(telefone));
        gateway.save(existente);
    }

}
