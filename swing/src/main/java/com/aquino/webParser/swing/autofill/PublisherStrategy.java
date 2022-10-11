package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.Publisher;

import java.util.List;

class PublisherStrategy implements AutoFill.AutoFillStrategy<Publisher> {

    private List<Row<Publisher>> rows;
    private final AutoFillService autoFillService;

    PublisherStrategy(AutoFillService autoFillService) {
        this.autoFillService = autoFillService;
    }

    @Override
    public List<Row<Publisher>> rows() {
        return rows;
    }

    @Override
    public void rows(List<Row<Publisher>> rows) {
        this.rows = rows;
    }

    @Override
    public void fill() {

    }
}
