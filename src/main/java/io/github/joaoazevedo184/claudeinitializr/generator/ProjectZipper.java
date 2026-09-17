package io.github.joaoazevedo184.claudeinitializr.generator;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ProjectZipper {

	public byte[] zip(List<GeneratedFile> files) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (ZipArchiveOutputStream zip = new ZipArchiveOutputStream(buffer)) {
			for (GeneratedFile file : files) {
				ZipArchiveEntry entry = new ZipArchiveEntry(file.path());
				entry.setUnixMode(file.unixMode());
				zip.putArchiveEntry(entry);
				zip.write(file.content().getBytes(StandardCharsets.UTF_8));
				zip.closeArchiveEntry();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return buffer.toByteArray();
	}

}
