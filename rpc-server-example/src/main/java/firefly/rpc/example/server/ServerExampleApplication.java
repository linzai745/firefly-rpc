package firefly.rpc.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class ServerExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerExampleApplication.class, args);
    }
}
