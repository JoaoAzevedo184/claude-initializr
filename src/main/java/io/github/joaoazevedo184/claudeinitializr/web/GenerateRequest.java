package io.github.joaoazevedo184.claudeinitializr.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record GenerateRequest(

		@NotBlank
		@Pattern(regexp = "^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$",
				message = "deve corresponder a ^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$")
		String group,

		@NotBlank
		@Pattern(regexp = "^[a-z][a-z0-9-]{0,49}$",
				message = "deve corresponder a ^[a-z][a-z0-9-]{0,49}$")
		String artifact,

		@NotBlank
		@Pattern(regexp = "^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$",
				message = "deve corresponder a ^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$")
		String packageName,

		@NotBlank
		String bootVersion,

		@NotBlank
		String javaVersion,

		@NotBlank
		String buildTool,

		List<@NotNull @Pattern(regexp = "^(data-jpa|security|data-redis|data-mongodb|kafka)$",
				message = "dependência desconhecida") String> dependencias,

		List<@NotNull @Pattern(regexp = "^(rules|commands|skills|agents|hooks|mcp)$",
				message = "componente desconhecido") String> componentes

) {
}
