package com.example.di;

import com.example.datasource.repository.GameRepository;
import com.example.domain.service.GameServiceImpl;
import com.example.domain.service.GameService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public GameService gameService(GameRepository repository) {
        return new GameServiceImpl(repository);
    }
}