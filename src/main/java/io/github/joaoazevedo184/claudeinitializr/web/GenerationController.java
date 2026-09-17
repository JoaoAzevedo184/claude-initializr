package io.github.joaoazevedo184.claudeinitializr.web;

import io.github.joaoazevedo184.claudeinitializr.generator.GeneratedFile;
import io.github.joaoazevedo184.claudeinitializr.generator.ProjectGenerator;
import io.github.joaoazevedo184.claudeinitializr.generator.ProjectZipper;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GenerationController {

	private final ProjectGenerator projectGenerator;
	private final ProjectZipper projectZipper;

	public GenerationController(ProjectGenerator projectGenerator, ProjectZipper projectZipper) {
		this.projectGenerator = projectGenerator;
		this.projectZipper = projectZipper;
	}

	@PostMapping(value = "/api/generate", produces = "application/zip")
	public ResponseEntity<byte[]> generate(@Valid @RequestBody GenerateRequest request) {
		List<GeneratedFile> files = projectGenerator.generate(request.group(), request.artifact(), request.packageName());
		byte[] zip = projectZipper.zip(files);

		ContentDisposition disposition = ContentDisposition.attachment()
				.filename(request.artifact() + "-claude.zip")
				.build();

		return ResponseEntity.ok()
				.contentType(MediaType.valueOf("application/zip"))
				.header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
				.body(zip);
	}

}
