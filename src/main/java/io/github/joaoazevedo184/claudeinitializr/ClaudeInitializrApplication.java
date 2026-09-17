package io.github.joaoazevedo184.claudeinitializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClaudeInitializrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaudeInitializrApplication.class, args);
	}

}
