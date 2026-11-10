package com.mecanica.oficina_customer_service.adapters.common;

import java.util.List;

public record ValidationErrorResponse(String mensagem, List<FieldError> erros) {

}
