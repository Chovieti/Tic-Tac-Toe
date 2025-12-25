package com.example.di;

import com.example.datasource.repository.GameRepository;
import com.example.datasource.service.GameServiceImpl;
import com.example.datasource.storage.GameStorage;
import com.example.datasource.storage.GameStorageImpl;
import com.example.domain.service.GameService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public GameStorage gameStorage() {
        return new GameStorageImpl();
    }

    @Bean
    public GameRepository gameRepository(GameStorage storage) {
        return new GameRepository(storage);
    }

    @Bean
    public GameService gameService(GameRepository repository) {
        return new GameServiceImpl(repository);
    }
}