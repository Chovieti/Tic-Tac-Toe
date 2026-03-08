package com.example.datasource.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class FieldConverter implements AttributeConverter<int[][], String> {
    private static final String ROW_DELIMITER = ";";
    private static final String CELL_DELIMITER = ",";
    @Override
    public String convertToDatabaseColumn(int[][] attribute) {
        if (attribute == null) return null;

        return Arrays.stream(attribute)
                .map(row -> Arrays.stream(row)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(CELL_DELIMITER)))
                .collect(Collectors.joining(ROW_DELIMITER));
    }

    @Override
    public int[][] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return new int[3][3];
        String[] rows = dbData.split(ROW_DELIMITER);
        int[][] field = new int[3][3];
        for (int i = 0; i < rows.length && i < 3; i++) {
            String[] cells = rows[i].split(CELL_DELIMITER);
            for (int j = 0; j < rows[i].length() && j < 3; j++) {
                field[i][j] = Integer.parseInt(cells[j]);
            }
        }
        return field;
    }
}
