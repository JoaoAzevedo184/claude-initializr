package io.github.joaoazevedo184.claudeinitializr.generator;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectZipperTest {

	private final ProjectZipper zipper = new ProjectZipper();

	@Test
	void zipContainsFileWithExpectedContentAndUnixMode() throws IOException {
		GeneratedFile claudeMd = new GeneratedFile("CLAUDE.md", "# CLAUDE.md\n\nconteúdo", 0644);

		byte[] zipBytes = zipper.zip(List.of(claudeMd));

		try (ZipFile zipFile = ZipFile.builder()
				.setSeekableByteChannel(new SeekableInMemoryByteChannel(zipBytes))
				.get()) {
			ZipArchiveEntry entry = zipFile.getEntry("CLAUDE.md");
			assertThat(entry).isNotNull();
			assertThat(entry.getUnixMode() & 0777).isEqualTo(0644);

			try (InputStream content = zipFile.getInputStream(entry)) {
				String actual = new String(content.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
				assertThat(actual).isEqualTo("# CLAUDE.md\n\nconteúdo");
			}
		}
	}

}
