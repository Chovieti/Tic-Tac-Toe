package com.example.datasource.repository;

import com.example.datasource.mapper.MapperDomainDatasource;
import com.example.datasource.model.DSCurrentGame;
import com.example.domain.model.CurrentGame;
import com.example.domain.model.LeaderboardEntry;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<CurrentGame> findAvailableGames(UUID id) {
        return dataRepository.findAvailableGames(id)
                .stream()
                .map(MapperDomainDatasource::toDomainCurrentGame)
                .toList();
    }

    public List<CurrentGame> findAllFinishedGamesByUserId(UUID id) {
        return dataRepository.findAllFinishedGamesByUserId(id)
                .stream()
                .map(MapperDomainDatasource::toDomainCurrentGame)
                .toList();
    }

    public List<LeaderboardEntry> getLeaderboard(int limit) {
        return dataRepository.getLeaderboardRaw(limit)
                .stream()
                .map(row -> new LeaderboardEntry(
                        (UUID) row[0],
                        (String) row[1],
                        ((Number) row[2]).doubleValue()
                ))
                .collect(Collectors.toList());
    }
}
