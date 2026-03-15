package com.example.datasource.repository;

import com.example.datasource.model.DSCurrentGame;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CurrentGameDataRepository extends CrudRepository<DSCurrentGame, UUID> {
    @Query("""
           SELECT g FROM DSCurrentGame g
           WHERE g.status NOT IN ('DRAW', 'WIN_PLAYER_X', 'WIN_PLAYER_O')
           AND (g.status = 'WAITING' OR g.playerXId = :userId OR g.playerOId = :userId) 
           """)
    List<DSCurrentGame> findAvailableGames(@Param("userId") UUID userId);
}
