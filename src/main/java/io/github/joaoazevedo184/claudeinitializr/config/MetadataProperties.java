package io.github.joaoazevedo184.claudeinitializr.config;

import io.github.joaoazevedo184.claudeinitializr.metadata.MetadataOption;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "claude-initializr")
public record MetadataProperties(
		List<MetadataOption> bootVersions,
		List<MetadataOption> javaVersions,
		List<MetadataOption> buildTools,
		List<MetadataOption> componentes
) {
}
