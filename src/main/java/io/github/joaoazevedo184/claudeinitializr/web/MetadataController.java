package io.github.joaoazevedo184.claudeinitializr.web;

import io.github.joaoazevedo184.claudeinitializr.config.MetadataProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetadataController {

	private final MetadataProperties metadataProperties;

	public MetadataController(MetadataProperties metadataProperties) {
		this.metadataProperties = metadataProperties;
	}

	@GetMapping("/api/metadata")
	public MetadataProperties metadata() {
		return metadataProperties;
	}

}
