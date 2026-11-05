package com.mecanica.oficina_customer_service.adapters.config;

import com.mecanica.oficina_customer_service.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_customer_service.application.cliente.usecase.AlterarClienteUseCase;
import com.mecanica.oficina_customer_service.application.cliente.usecase.CadastrarClienteUseCase;
import com.mecanica.oficina_customer_service.application.cliente.usecase.ConsultarClienteUseCase;
import com.mecanica.oficina_customer_service.application.cliente.usecase.DeletarClienteUseCase;
import com.mecanica.oficina_customer_service.application.cliente.usecase.ListarClientesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    /* Cliente */
    @Bean
    CadastrarClienteUseCase cadastrarClienteUseCase(ClienteGateway gateway) {
        return new CadastrarClienteUseCase(gateway);
    }
    @Bean
    ConsultarClienteUseCase consultarClienteUseCase(ClienteGateway gateway) {
        return new ConsultarClienteUseCase(gateway);
    }
    @Bean
    ListarClientesUseCase listarClientesUseCase(ClienteGateway gateway) {
        return new ListarClientesUseCase(gateway);
    }
    @Bean
    AlterarClienteUseCase alterarClienteUseCase(ClienteGateway gateway) {
        return new AlterarClienteUseCase(gateway);
    }
    @Bean
    DeletarClienteUseCase deletarClienteUseCase(ClienteGateway gateway) {
        return new DeletarClienteUseCase(gateway);
    }
}
