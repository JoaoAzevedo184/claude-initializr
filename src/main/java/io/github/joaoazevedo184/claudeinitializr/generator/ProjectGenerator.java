package io.github.joaoazevedo184.claudeinitializr.generator;

import io.github.joaoazevedo184.claudeinitializr.template.TemplateRenderer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ProjectGenerator {

	private static final String CLAUDE_MD_TEMPLATE = "claude-templates/CLAUDE.md.mustache";
	private static final int FILE_MODE = 0644;
	private static final int EXECUTABLE_MODE = 0755;

	// Sempre gerados, independente dos componentes escolhidos.
	private static final List<String> BASE_FILES = List.of(
			".claude/settings.json",
			".claude/settings.local.json");

	// id do componente -> arquivos gerados. O template fica em claude-templates/ com o mesmo
	// caminho, sem o prefixo ".claude/" (ou "."), mais o sufixo ".mustache".
	private static final Map<String, List<String>> COMPONENT_FILES = new LinkedHashMap<>();

	static {
		COMPONENT_FILES.put("rules", List.of(".claude/rules/code-style.md", ".claude/rules/testing.md"));
		COMPONENT_FILES.put("commands", List.of(".claude/commands/test.md", ".claude/commands/review.md"));
		COMPONENT_FILES.put("skills", List.of(".claude/skills/spring-endpoint/SKILL.md"));
		COMPONENT_FILES.put("agents", List.of(".claude/agents/code-reviewer.md"));
		COMPONENT_FILES.put("hooks", List.of(".claude/hooks/validate-bash.sh"));
		COMPONENT_FILES.put("mcp", List.of(".mcp.json"));
	}

	// id da dependência -> flag do contexto Mustache (usada em CLAUDE.md.mustache).
	private static final Map<String, String> DEPENDENCY_FLAGS = Map.of(
			"data-jpa", "temJpa",
			"security", "temSecurity",
			"data-redis", "temRedis",
			"data-mongodb", "temMongo",
			"kafka", "temKafka");

	// id da dependência -> arquivos extras, gerados só se o componente indicado também foi escolhido.
	private static final List<DependencyFile> DEPENDENCY_FILES = List.of(
			new DependencyFile("data-jpa", "rules", ".claude/rules/persistence.md"),
			new DependencyFile("security", "rules", ".claude/rules/security.md"),
			new DependencyFile("security", "agents", ".claude/agents/security-auditor.md"));

	private record DependencyFile(String dependency, String component, String path) {
	}

	private final TemplateRenderer templateRenderer;

	public ProjectGenerator(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

	public List<GeneratedFile> generate(String group, String artifact, String packageName,
			String bootVersion, String javaVersion, String buildTool, List<String> dependencias, List<String> componentes) {
		Set<String> deps = dependencias == null ? Set.of() : Set.copyOf(dependencias);
		Set<String> escolhidos = componentes == null ? Set.of() : Set.copyOf(componentes);
		String packagePath = packageName.replace('.', '/');

		Map<String, Object> context = new HashMap<>(Map.of(
				"artifact", artifact,
				"group", group,
				"packageName", packageName,
				"packagePath", packagePath,
				"javaVersion", javaVersion,
				"bootVersion", bootVersion,
				"buildTool", buildTool,
				"comandoRun", "./mvnw spring-boot:run",
				"comandoTest", "./mvnw test",
				"temHooks", escolhidos.contains("hooks")
		));
		DEPENDENCY_FLAGS.forEach((id, flag) -> context.put(flag, deps.contains(id)));

		List<GeneratedFile> files = new ArrayList<>();
		files.add(new GeneratedFile("CLAUDE.md", templateRenderer.render(CLAUDE_MD_TEMPLATE, context), FILE_MODE));
		BASE_FILES.forEach(path -> files.add(render(path, context)));
		COMPONENT_FILES.forEach((id, paths) -> {
			if (escolhidos.contains(id)) {
				paths.forEach(path -> files.add(render(path, context)));
			}
		});
		DEPENDENCY_FILES.stream()
				.filter(f -> deps.contains(f.dependency()) && escolhidos.contains(f.component()))
				.forEach(f -> files.add(render(f.path(), context)));
		return files;
	}

	private GeneratedFile render(String path, Map<String, Object> context) {
		String relative = path.startsWith(".claude/") ? path.substring(".claude/".length()) : path.substring(1);
		String content = templateRenderer.render("claude-templates/" + relative + ".mustache", context);
		return new GeneratedFile(path, content, path.endsWith(".sh") ? EXECUTABLE_MODE : FILE_MODE);
	}

}
