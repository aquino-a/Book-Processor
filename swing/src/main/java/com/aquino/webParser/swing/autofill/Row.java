package com.aquino.webParser.swing.autofill;

public class Row<T> {

    private final T object;
    private boolean isSelected;
    private String link;

    public Row(T object) {
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
}
