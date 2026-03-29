package com.example.datasource.mapper;

import com.example.datasource.model.DSCurrentGame;
import com.example.datasource.model.DSGameField;
import com.example.datasource.model.DSUser;
import com.example.domain.model.CurrentGame;
import com.example.domain.model.GameField;
import com.example.domain.model.User;

public class MapperDomainDatasource {
    public static CurrentGame toDomainCurrentGame(DSCurrentGame game) {
        GameField domainField = toDomainField(game.getField());
        return new CurrentGame(
                game.getId(),
                domainField,
                game.getType(),
                game.getStatus(),
                game.getPlayerXId(),
                game.getPlayerOId()
        );
    }

    private static GameField toDomainField(DSGameField field) {
        return new GameField(copyField(field.getField()));
    }

    public static User toDomainUser(DSUser user) {
        return new User(user.getId(), user.getLogin(), user.getPassword());
    }

    public static DSCurrentGame toDSCurrentGame(CurrentGame game) {
        DSGameField dsField = toDSField(game.getField());
        return new DSCurrentGame(
                game.getId(),
                dsField,
                game.getType(),
                game.getStatus(),
                game.getPlayerXId(),
                game.getPlayerOId()
        );
    }

    private static DSGameField toDSField(GameField field) {
        return new DSGameField(copyField(field.getField()));
    }

    public static DSUser toDSUser(User user) {
        return new DSUser(user.getId(), user.getLogin(), user.getPassword());
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
