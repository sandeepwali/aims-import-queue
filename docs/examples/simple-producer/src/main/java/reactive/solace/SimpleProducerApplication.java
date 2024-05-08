package reactive.solace;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SimpleProducerApplication {

	private static final Logger log = LoggerFactory.getLogger(SimpleProducerApplication.class);

	private static final UUID sensorIdentifier = UUID.randomUUID();
	private static final Random random = new Random(System.currentTimeMillis());
	private static final int RANDOM_MULTIPLIER = 100;

	public static void main(String[] args) {
		SpringApplication.run(FahrenheitTempSource.class);
	}

	/* 
	 * Basic Supplier which sends messages every X milliseconds
	 * Configurable using spring.cloud.stream.poller.fixed-delay 
	 */
	@Bean
	public Supplier<SensorReading> emitSensorReading() {
		return () -> {
			double temperature = random.nextDouble() * RANDOM_MULTIPLIER;

			SensorReading reading = new SensorReading();
			reading.setSensorID(sensorIdentifier.toString());
			reading.setTemperature(temperature);
			reading.setBaseUnit(BaseUnit.FAHRENHEIT);

			log.info("Emitting " + reading);

			return reading;
		};
	}

}
