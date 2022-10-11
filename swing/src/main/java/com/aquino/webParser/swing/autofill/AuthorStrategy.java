package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.Author;

class AuthorStrategy extends AutoFillStrategy<Author> {

    AuthorStrategy(AutoFillService autoFillService) {
        super(autoFillService);
    }

    @Override
    protected void insert(Row<Author> authorRow) {
        var author = authorRow.object();
        var id = autoFillService.insertAuthor(author);
        var link = autoFillService.getAuthorLink(id);

        author.setId(id);
        authorRow.link(link);
        authorRow.ids().updateBook();
    }
}
