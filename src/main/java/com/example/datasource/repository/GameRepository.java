package com.example.datasource.repository;

import com.example.datasource.mapper.MapperDomainDatasource;
import com.example.datasource.model.DSCurrentGame;
import com.example.domain.model.CurrentGame;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class GameRepository {
    private final CurrentGameDataRepository dataRepository;

    public GameRepository(CurrentGameDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void saveGame(CurrentGame game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        DSCurrentGame dsGame = MapperDomainDatasource.toDSCurrentGame(game);
        dataRepository.save(dsGame);
    }

    public Optional<CurrentGame> getGame(UUID id) {
        return dataRepository.findById(id)
                .map(MapperDomainDatasource::toDomainCurrentGame);
    }
}
