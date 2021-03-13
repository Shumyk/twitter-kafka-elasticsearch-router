package rocks.shumyk.route.twitter.kafka.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class StatusController {

	@GetMapping("/up")
	public ResponseEntity<String> up() {
		log.info("Called status for 'up'");
		return ResponseEntity.ok("up");
	}
}
