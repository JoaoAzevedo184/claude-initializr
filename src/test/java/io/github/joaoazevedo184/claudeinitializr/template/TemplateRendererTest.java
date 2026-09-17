package io.github.joaoazevedo184.claudeinitializr.template;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateRendererTest {

	private final TemplateRenderer renderer = new TemplateRenderer();

	@Test
	void rendersClaudeMdTemplateWithContext() {
		Map<String, Object> context = Map.of(
				"artifact", "minha-api",
				"group", "com.exemplo",
				"packageName", "com.exemplo.minhaapi",
				"packagePath", "com/exemplo/minhaapi",
				"javaVersion", "21",
				"bootVersion", "4.1.1",
				"buildTool", "maven",
				"comandoRun", "./mvnw spring-boot:run",
				"comandoTest", "./mvnw test"
		);

		String result = renderer.render("claude-templates/CLAUDE.md.mustache", context);

		assertThat(result).contains("minha-api");
		assertThat(result).contains("com.exemplo.minhaapi");
		assertThat(result).contains("com/exemplo/minhaapi");
		assertThat(result).contains("21");
		assertThat(result).contains("4.1.1");
	}

	@Test
	void throwsWhenTemplateDoesNotExist() {
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
				() -> renderer.render("claude-templates/does-not-exist.mustache", Map.of()));
	}

}
