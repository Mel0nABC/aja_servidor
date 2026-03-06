package dev.aja.aja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "dev.aja.aja.auth" })
public class AjaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AjaApplication.class, args);
	}

}
