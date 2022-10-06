package com.aquino.webParser.swing.autofill;

public class Row {

    private final Object object;
    private boolean isSelected;

    public Row(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
