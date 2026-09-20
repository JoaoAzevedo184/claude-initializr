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
				"4.1.1", "17", "maven", List.of());

		GeneratedFile claudeMd = files.get(0);
		assertThat(claudeMd.path()).isEqualTo("CLAUDE.md");
		assertThat(claudeMd.unixMode()).isEqualTo(0644);
		assertThat(claudeMd.content()).contains("minha-api");
		assertThat(claudeMd.content()).contains("com.exemplo.minhaapi");
		assertThat(claudeMd.content()).contains("com/exemplo/minhaapi");
		assertThat(claudeMd.content()).contains("17");
		assertThat(claudeMd.content()).contains("4.1.1");
	}

	@Test
	void withoutComponentsGeneratesOnlyClaudeMdAndSettings() {
		List<GeneratedFile> files = generator.generate(
				"com.exemplo", "minha-api", "com.exemplo.minhaapi",
				"4.1.1", "21", "maven", null);

		assertThat(files).extracting(GeneratedFile::path)
				.containsExactly("CLAUDE.md", ".claude/settings.json", ".claude/settings.local.json");
		assertThat(files.get(1).content()).doesNotContain("hooks");
	}

	@Test
	void hooksComponentGeneratesExecutableScriptAndRegistersItInSettings() {
		List<GeneratedFile> files = generator.generate(
				"com.exemplo", "minha-api", "com.exemplo.minhaapi",
				"4.1.1", "21", "maven", List.of("hooks"));

		GeneratedFile hook = file(files, ".claude/hooks/validate-bash.sh");
		assertThat(hook.unixMode()).isEqualTo(0755);
		assertThat(file(files, ".claude/settings.json").content()).contains("validate-bash.sh");
	}

	@Test
	void everyComponentRendersItsFilesWithoutLeftoverMustacheTags() {
		List<GeneratedFile> files = generator.generate(
				"com.exemplo", "minha-api", "com.exemplo.minhaapi",
				"4.1.1", "21", "maven", List.of("rules", "commands", "skills", "agents", "hooks", "mcp"));

		assertThat(files).extracting(GeneratedFile::path).contains(
				".claude/rules/code-style.md", ".claude/rules/testing.md",
				".claude/commands/test.md", ".claude/commands/review.md",
				".claude/skills/spring-endpoint/SKILL.md", ".claude/agents/code-reviewer.md",
				".claude/hooks/validate-bash.sh", ".mcp.json");
		assertThat(files).allSatisfy(f -> assertThat(f.content()).doesNotContain("{{"));
		assertThat(files).filteredOn(f -> !f.path().endsWith(".sh"))
				.allSatisfy(f -> assertThat(f.unixMode()).isEqualTo(0644));
	}

	private static GeneratedFile file(List<GeneratedFile> files, String path) {
		return files.stream().filter(f -> f.path().equals(path)).findFirst().orElseThrow();
	}

}
