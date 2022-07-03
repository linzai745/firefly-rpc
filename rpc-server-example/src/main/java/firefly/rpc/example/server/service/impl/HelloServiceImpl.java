package firefly.rpc.example.server.service.impl;

import firefly.rpc.example.server.service.HelloService;
import firefly.rpc.server.annotation.RpcService;
import org.springframework.stereotype.Service;

@RpcService(serviceInterface = HelloService.class)
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello() {
        return "Hello Firefly!";
    }
}
