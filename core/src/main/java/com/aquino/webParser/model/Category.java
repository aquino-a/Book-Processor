package com.aquino.webParser.model;

import java.util.List;

public class Category {
    private String name, code;
    private List<Category> subCategories;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public List<Category> getSubCategories() {
        return subCategories;
    }
    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }
}
