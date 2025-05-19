package uz.fido.paymentservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PaymentServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Payment Service...");
		SpringApplication.run(PaymentServiceApplication.class, args);
		logger.info("Payment Service started successfully");
	}
}