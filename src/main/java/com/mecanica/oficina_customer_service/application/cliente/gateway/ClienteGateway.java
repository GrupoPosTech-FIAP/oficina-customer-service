package com.mecanica.oficina_customer_service.application.cliente.gateway;

import com.mecanica.oficina_customer_service.domain.cliente.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteGateway {
    boolean existsByDocumento(String documento);
    Optional<Cliente> findByDocumentoAtivo(String documento);

    default Cliente buscarPorDocumentoOuFalhar(String documento) {
        return findByDocumentoAtivo(documento)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    List<Cliente> findAllAtivos();
    Cliente save(Cliente cliente);
    void softDelete(String documento);
    Optional<Cliente> findByIdAtivo(String id);
}