package com.aquino.webParser.swing.autofill;

public class Row {

    private final Object object;
    private boolean isSelected;
    private String link;

    public Row(Object object) {
        this.object = object;
    }

    public Object object() {
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
