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

    @Query("""
           SELECT g FROM DSCurrentGame g
           WHERE g.status IN ('DRAW', 'WIN_PLAYER_X', 'WIN_PLAYER_O')
           AND (g.playerXId = :userId OR g.playerOId = :userId)
           """)
    List<DSCurrentGame> findAllFinishedGamesByUserId(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
                 player_id,
                 login,
                 wins * 100.0 / (wins + losses + draws) AS win_ratio
            FROM (
                 SELECT
                     player_id,
                     SUM(is_win) AS wins,
                     SUM(is_loss) AS losses,
                     SUM(is_draw) AS draws
                 FROM (
                     SELECT player_x_id AS player_id,
                           CASE WHEN game_status = 'WIN_PLAYER_X' THEN 1 ELSE 0 END AS is_win,
                           CASE WHEN game_status = 'WIN_PLAYER_O' THEN 1 ELSE 0 END AS is_loss,
                           CASE WHEN game_status = 'DRAW' THEN 1 ELSE 0 END AS is_draw
                    FROM current_games
                    WHERE player_x_id IS NOT NULL
                      AND game_status IN ('WIN_PLAYER_X', 'WIN_PLAYER_O', 'DRAW')
                    UNION ALL
                    SELECT player_o_id AS player_id,
                           CASE WHEN game_status = 'WIN_PLAYER_O' THEN 1 ELSE 0 END AS is_win,
                           CASE WHEN game_status = 'WIN_PLAYER_X' THEN 1 ELSE 0 END AS is_loss,
                           CASE WHEN game_status = 'DRAW' THEN 1 ELSE 0 END AS is_draw
                    FROM current_games
                    WHERE player_o_id IS NOT NULL
                      AND game_status IN ('WIN_PLAYER_X', 'WIN_PLAYER_O', 'DRAW')
                ) AS stats
                GROUP BY player_id
            ) AS aggregated
            JOIN users ON aggregated.player_id = users.id
            ORDER BY win_ratio DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> getLeaderboardRaw(@Param("limit") int limit);
}
