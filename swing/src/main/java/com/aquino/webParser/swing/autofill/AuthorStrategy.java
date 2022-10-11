package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.autofill.AutoFillService;
import com.aquino.webParser.model.Author;

import java.util.List;

class AuthorStrategy implements AutoFill.AutoFillStrategy<Author> {

    private List<Row<Author>> rows;
    private final AutoFillService autoFillService;

    AuthorStrategy(AutoFillService autoFillService) {
        this.autoFillService = autoFillService;
    }

    @Override
    public List<Row<Author>> rows() {
        return rows;
    }

    @Override
    public void rows(List<Row<Author>> rows) {
        this.rows = rows;
    }

    @Override
    public void fill() {
        rows.stream()
            .filter(r -> r.object() != null)
            .filter(r -> r.isSelected())
            .forEach(this::insert);
    }

    private void insert(Row<Author> authorRow) {
        var author = authorRow.object();
        var id = autoFillService.insertAuthor(author);
        var link = autoFillService.getAuthorLink(id);

        author.setId(id);
        authorRow.link(link);
        authorRow.ids().updateBook();
    }
}
