package com.aquino.webParser;

public class ExtraInfo {

    private int columnNumber;
    private String value;
    private Type type;
    private String name;

    public ExtraInfo(int columnNumber, String value, Type type) {
        this.columnNumber = columnNumber;
        this.value = value;
        this.type = type;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Type {
        HyperLink
    }
}


