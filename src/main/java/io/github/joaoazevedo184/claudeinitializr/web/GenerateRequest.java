package io.github.joaoazevedo184.claudeinitializr.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
		String buildTool

) {
}
