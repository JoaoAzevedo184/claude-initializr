package io.github.joaoazevedo184.claudeinitializr.web;

import java.util.Map;

public record ValidationErrorResponse(String erro, String mensagem, Map<String, String> campos) {
}
