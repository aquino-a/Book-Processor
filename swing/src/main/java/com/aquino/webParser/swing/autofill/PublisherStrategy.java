package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.Publisher;

import java.util.List;

class PublisherStrategy extends AutoFillStrategy<Publisher> {

    PublisherStrategy(AutoFillService autoFillService) {
        super(autoFillService);
    }

    @Override
    protected void insert(Row<Publisher> authorRow) {

    }
}
