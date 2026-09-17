package io.github.joaoazevedo184.claudeinitializr.template;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class TemplateRenderer {

	public String render(String templatePath, Map<String, Object> context) {
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(templatePath)) {
			if (input == null) {
				throw new IllegalArgumentException("Template não encontrado: " + templatePath);
			}
			Template template = Mustache.compiler().compile(new InputStreamReader(input, StandardCharsets.UTF_8));
			return template.execute(context);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
