package eu.creativeone.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "eu.creativeone")
public class FeignConfiguration {

}
