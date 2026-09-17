package io.github.joaoazevedo184.claudeinitializr.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> campos = new LinkedHashMap<>();
		for (FieldError error : exception.getBindingResult().getFieldErrors()) {
			campos.put(error.getField(), error.getDefaultMessage());
		}
		ValidationErrorResponse body = new ValidationErrorResponse("VALIDACAO", "Campo invalido", campos);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

}
