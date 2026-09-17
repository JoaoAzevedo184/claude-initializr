package io.github.joaoazevedo184.claudeinitializr.web;

import io.github.joaoazevedo184.claudeinitializr.generator.GeneratedFile;
import io.github.joaoazevedo184.claudeinitializr.generator.ProjectGenerator;
import io.github.joaoazevedo184.claudeinitializr.generator.ProjectZipper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenerationController.class)
class GenerationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProjectGenerator projectGenerator;

	@MockitoBean
	private ProjectZipper projectZipper;

	@Test
	void generateReturnsZipForValidRequest() throws Exception {
		when(projectGenerator.generate(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(List.of(new GeneratedFile("CLAUDE.md", "# CLAUDE.md", 0644)));
		when(projectZipper.zip(any())).thenReturn(new byte[] {1, 2, 3});

		mockMvc.perform(post("/api/generate")
						.contentType("application/json")
						.content("""
								{"group":"com.exemplo","artifact":"minha-api","packageName":"com.exemplo.minhaapi","bootVersion":"4.1.1","javaVersion":"21","buildTool":"maven"}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/zip"))
				.andExpect(header().string("Content-Disposition", "attachment; filename=\"minha-api-claude.zip\""));
	}

	@Test
	void rejectsPathTraversalInArtifact() throws Exception {
		mockMvc.perform(post("/api/generate")
						.contentType("application/json")
						.content("""
								{"group":"com.exemplo","artifact":"../evil","packageName":"com.exemplo.minhaapi","bootVersion":"4.1.1","javaVersion":"21","buildTool":"maven"}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erro").value("VALIDACAO"))
				.andExpect(jsonPath("$.campos.artifact").exists());
	}

}
