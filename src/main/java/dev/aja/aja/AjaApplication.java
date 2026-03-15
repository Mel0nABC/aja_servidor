package dev.aja.aja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * clase de inicialización de la aplicación
 */
@SpringBootApplication
public class AjaApplication {

	/**
     * Constructor creado para ignorar warnings cuando se crea javadoc
     */
    public AjaApplication() {
    }

	/**
	 * Método main para inicializar la aplicación
	 * 
	 * @param args posibles argumentos que pueden enviarse al iniciar la aplicación
	 */
	public static void main(String[] args) {
		SpringApplication.run(AjaApplication.class, args);
	}

}
