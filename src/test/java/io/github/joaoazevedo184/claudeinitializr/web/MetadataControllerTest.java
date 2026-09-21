package io.github.joaoazevedo184.claudeinitializr.web;

import io.github.joaoazevedo184.claudeinitializr.config.MetadataProperties;
import io.github.joaoazevedo184.claudeinitializr.metadata.DependencyOption;
import io.github.joaoazevedo184.claudeinitializr.metadata.MetadataOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetadataController.class)
class MetadataControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MetadataProperties metadataProperties;

	@Test
	void returnsMetadataCatalog() throws Exception {
		when(metadataProperties.bootVersions())
				.thenReturn(List.of(new MetadataOption("4.1.1", "4.1.1", true)));
		when(metadataProperties.javaVersions())
				.thenReturn(List.of(
						new MetadataOption("21", "21", true),
						new MetadataOption("17", "17", false)));
		when(metadataProperties.buildTools())
				.thenReturn(List.of(new MetadataOption("maven", "Maven", true)));
		when(metadataProperties.dependencias())
				.thenReturn(List.of(new DependencyOption("data-jpa", "Spring Data JPA", "SQL", "desc")));
		when(metadataProperties.componentes())
				.thenReturn(List.of(new MetadataOption("rules", "rules/", true)));

		mockMvc.perform(get("/api/metadata"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.bootVersions[0].id").value("4.1.1"))
				.andExpect(jsonPath("$.javaVersions[0].padrao").value(true))
				.andExpect(jsonPath("$.javaVersions[1].id").value("17"))
				.andExpect(jsonPath("$.buildTools[0].id").value("maven"))
				.andExpect(jsonPath("$.dependencias[0].id").value("data-jpa"))
				.andExpect(jsonPath("$.componentes[0].id").value("rules"));
	}

}
