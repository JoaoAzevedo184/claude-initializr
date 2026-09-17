package io.github.joaoazevedo184.claudeinitializr.generator;

import io.github.joaoazevedo184.claudeinitializr.template.TemplateRenderer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectGeneratorTest {

	private final ProjectGenerator generator = new ProjectGenerator(new TemplateRenderer());

	@Test
	void generatesClaudeMdWithSubstitutedContext() {
		List<GeneratedFile> files = generator.generate(
				"com.exemplo", "minha-api", "com.exemplo.minhaapi",
				"4.1.1", "17", "maven");

		assertThat(files).hasSize(1);
		GeneratedFile claudeMd = files.get(0);
		assertThat(claudeMd.path()).isEqualTo("CLAUDE.md");
		assertThat(claudeMd.unixMode()).isEqualTo(0644);
		assertThat(claudeMd.content()).contains("minha-api");
		assertThat(claudeMd.content()).contains("com.exemplo.minhaapi");
		assertThat(claudeMd.content()).contains("com/exemplo/minhaapi");
		assertThat(claudeMd.content()).contains("17");
		assertThat(claudeMd.content()).contains("4.1.1");
	}

}
