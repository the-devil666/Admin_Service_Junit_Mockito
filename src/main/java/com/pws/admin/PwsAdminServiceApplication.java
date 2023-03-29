package com.pws.admin;

import com.pws.admin.controller.MyWebSocketClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.pws.admin.utility.AuditAwareImpl;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.pws.admin.*"})
@EnableCaching
@EnableEurekaClient

public class PwsAdminServiceApplication {



	public static void main(String[] args) throws Exception {
		SpringApplication.run(PwsAdminServiceApplication.class, args);
		MyWebSocketClient client = new MyWebSocketClient("ws://localhost:8081/ws");



	}
	@Bean
    public AuditorAware<String> auditorAware() {
        return new AuditAwareImpl();
    }



}
