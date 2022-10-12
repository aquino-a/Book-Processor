package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.Publisher;

class PublisherStrategy extends AutoFillStrategy<Publisher> {

    PublisherStrategy(AutoFillService autoFillService) {
        super(autoFillService);
    }

    @Override
    protected void insert(Row<Publisher> publisherRow) {
        var publisher = publisherRow.object();
        var id = autoFillService.insertPublisher(publisher);
        var link = autoFillService.getPublisherLink(id);

        publisher.setId(id);
        publisherRow.link(link);
        publisherRow.ids().updateBook();
    }
}
