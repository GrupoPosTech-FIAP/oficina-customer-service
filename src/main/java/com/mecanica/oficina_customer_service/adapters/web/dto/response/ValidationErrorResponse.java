package com.mecanica.oficina_customer_service.adapters.web.dto.response;

import java.util.List;

public record ValidationErrorResponse(
    String mensagem,
    List<FieldError> erros
) {
    public record FieldError(String campo, String mensagem) {}
}
