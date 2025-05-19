package uz.fido.surekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	private static final Logger logger = LoggerFactory.getLogger(EurekaServerApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Eureka Server...");
		SpringApplication.run(EurekaServerApplication.class, args);
		logger.info("Eureka Server started successfully");
	}
}