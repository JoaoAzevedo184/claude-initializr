package io.github.joaoazevedo184.claudeinitializr.generator;

import io.github.joaoazevedo184.claudeinitializr.template.TemplateRenderer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ProjectGenerator {

	private static final String CLAUDE_MD_TEMPLATE = "claude-templates/CLAUDE.md.mustache";
	private static final int FILE_MODE = 0644;

	private final TemplateRenderer templateRenderer;

	public ProjectGenerator(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

	public List<GeneratedFile> generate(String group, String artifact, String packageName,
			String bootVersion, String javaVersion, String buildTool) {
		String packagePath = packageName.replace('.', '/');

		Map<String, Object> context = Map.of(
				"artifact", artifact,
				"group", group,
				"packageName", packageName,
				"packagePath", packagePath,
				"javaVersion", javaVersion,
				"bootVersion", bootVersion,
				"buildTool", buildTool,
				"comandoRun", "./mvnw spring-boot:run",
				"comandoTest", "./mvnw test"
		);

		String claudeMd = templateRenderer.render(CLAUDE_MD_TEMPLATE, context);
		return List.of(new GeneratedFile("CLAUDE.md", claudeMd, FILE_MODE));
	}

}
