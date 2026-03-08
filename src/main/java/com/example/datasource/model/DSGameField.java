package com.example.datasource.model;

import com.example.datasource.converter.FieldConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

@Embeddable
public class DSGameField {
    @Convert(converter =  FieldConverter.class)
    private int[][] field;

    public DSGameField() {}

    public DSGameField(int[][] field) {
        this.field = field;
    }

    public int[][] getField() {
        return field;
    }
}
