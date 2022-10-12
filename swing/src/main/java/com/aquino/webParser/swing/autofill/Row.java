package com.aquino.webParser.swing.autofill;

import com.aquino.webParser.model.BookWindowIds;

public class Row<T> {

    private final BookWindowIds parentIds;
    private final T object;
    private boolean isSelected;
    private String link;

    public Row(T object) {
        this(null, object);
    }

    public Row(BookWindowIds parentIds, T object) {
        this.parentIds = parentIds;
        this.object = object;
    }

    public T object() {
        return object;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void isSelected(boolean selected) {
        isSelected = selected;
    }

    public String link() {
        return link;
    }

    public void link(String link) {
        this.link = link;
    }

    public BookWindowIds ids() {
        return parentIds;
    }
}
