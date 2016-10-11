package com.upwork.garrett;

/**
 * Created by garrethdottin on 10/5/16.
 */
public class Scientist {
    private String text = "";
    private String title = "";
    private Integer century;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public Integer getCentury() {
        return century;
    }

    public void setCentury(Integer century) {
        this.century = century;
    }
}
