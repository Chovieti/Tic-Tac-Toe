package com.example.web.mapper;

import com.example.domain.model.CurrentGame;
import com.example.domain.model.GameField;
import com.example.domain.model.User;
import com.example.web.dto.WebCurrentGame;
import com.example.web.dto.WebGameField;
import com.example.web.dto.WebUser;

import java.util.HashSet;

public class MapperDomainWeb {
    public static CurrentGame toDomainCurrentGame(WebCurrentGame game) {
        GameField domainField = toDomainField(game.field());
        return new CurrentGame(
                game.id(),
                domainField,
                game.type(),
                game.status(),
                game.playerXId(),
                game.playerOId()
        );
    }

    private static GameField toDomainField(WebGameField field) {
        return new GameField(copyField(field.field()));
    }

    public static WebCurrentGame toWebCurrentGame(CurrentGame game) {
        WebGameField webField = toWebField(game.getField());
        return new WebCurrentGame(
                game.getId(),
                webField,
                game.getType(),
                game.getStatus(),
                game.getPlayerXId(),
                game.getPlayerOId()
        );
    }

    private static WebGameField toWebField(GameField field) {
        return new WebGameField(copyField(field.getField()));
    }

    public static WebUser toWebUser(User user) {
        return new WebUser(user.getId(), user.getLogin(), new HashSet<>());
    }

    private static int[][] copyField(int[][] field) {
        int[][] newField = new int[3][3];
        if (field == null) return newField;

        for (int i = 0; i < 3; i++) {
            if (rowExists(field, i)) {
                System.arraycopy(field[i], 0, newField[i], 0, Math.min(3, field[i].length));
            }
        }
        return newField;
    }

    private static boolean rowExists(int[][] field, int i) {
        return (field.length > i && field[i] != null);
    }
}
