package io.github.joaoazevedo184.claudeinitializr.generator;

import io.github.joaoazevedo184.claudeinitializr.template.TemplateRenderer;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Hook sem bit de execução falha em silêncio: confere o modo dentro do zip real, não só no GeneratedFile.
class GeneratedZipTest {

	@Test
	void hookScriptIsExecutableInsideZipAndOtherFilesAreNot() throws IOException {
		List<GeneratedFile> files = new ProjectGenerator(new TemplateRenderer()).generate(
				"com.exemplo", "minha-api", "com.exemplo.minhaapi",
				"4.1.1", "21", "maven", List.of("rules", "hooks", "mcp"));

		byte[] zipBytes = new ProjectZipper().zip(files);

		try (ZipFile zip = ZipFile.builder()
				.setSeekableByteChannel(new SeekableInMemoryByteChannel(zipBytes)).get()) {
			ZipArchiveEntry hook = zip.getEntry(".claude/hooks/validate-bash.sh");
			assertThat(hook).isNotNull();
			assertThat(hook.getUnixMode() & 0777).isEqualTo(0755);
			assertThat(zip.getEntry(".claude/settings.json").getUnixMode() & 0777).isEqualTo(0644);
			assertThat(zip.getEntry(".mcp.json").getUnixMode() & 0777).isEqualTo(0644);
		}
	}

}
