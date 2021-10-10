package se.bahram.thing1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@SpringBootApplication
public class Thing1Application {

	public static void main(String[] args) {
		SpringApplication.run(Thing1Application.class, args);
	}

	@Bean
	WebClient client() {
		return WebClient.create("http://localhost:7634/aircraft");
	}

}

@RestController
@AllArgsConstructor
class Thing1RestController {

	private final WebClient client;

	@GetMapping("/reqresp")
	Mono<AirCraft> reqResp() {
		return client.get()
				.retrieve()
				.bodyToFlux(AirCraft.class)
				.next();
	}
}

@Controller
@AllArgsConstructor
class Thing1RSocketController {

	private final WebClient client;

	@MessageMapping("r-socket")
	Flux<AirCraft> reqResp(Mono<Instant> tsMono) {

		return tsMono.doOnNext(ts -> System.out.println(" " + ts))
				.thenMany(
						client.get()
								.retrieve()
								.bodyToFlux(AirCraft.class)
				);


	}
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class AirCraft {
	private String classsign, reg, flightno, type;
	private int altitude, heading, speed;
	private double lat, lon;
}
