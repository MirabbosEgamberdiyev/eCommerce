package uz.fido.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.fido.productservice.dto.UserDto;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8081}")
public interface UserClient {

    @GetMapping("/api/users/{email}")
    UserDto getUserByEmail(@PathVariable("email") String userId);
}
