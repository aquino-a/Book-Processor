package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.Author;

import java.util.List;

public abstract class AutoFillStrategy<T> implements AutoFill.AutoFillStrategy<T> {
    protected final AutoFillService autoFillService;
    private List<Row<T>> rows;

    public AutoFillStrategy(AutoFillService autoFillService) {
        this.autoFillService = autoFillService;
    }

    @Override
    public List<Row<T>> rows() {
        return rows;
    }

    @Override
    public void rows(List<Row<T>> rows) {
        this.rows = rows;
    }

    @Override
    public void fill() {
        rows.stream()
            .filter(r -> r.object() != null)
            .filter(r -> r.isSelected())
            .forEach(this::insert);
    }

    protected abstract void insert(Row<T> authorRow);
}
