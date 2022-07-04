package firefly.rpc.example.client.controller;

import com.firefly.rpc.client.annotation.FireflyClient;
import firefly.rpc.example.client.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class HelloController {
    
    @FireflyClient
    private HelloService helloService;
    
    @GetMapping("/sayHello")
    public String sayHello() {
        return helloService.sayHello();
    }
}
