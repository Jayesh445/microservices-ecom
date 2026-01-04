package works.jayesh.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("\n" +
				"===============================================\n" +
				"   E-Commerce Application Started Successfully\n" +
				"   API Documentation: http://localhost:8080\n" +
				"===============================================\n");
	}

}
