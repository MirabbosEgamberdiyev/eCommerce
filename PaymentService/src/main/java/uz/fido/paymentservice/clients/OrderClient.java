package uz.fido.paymentservice.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.fido.paymentservice.dto.OrderResponse;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {
    @GetMapping("/api/orders/{id}")
    OrderResponse getOrderById(@PathVariable("id") String id);
}