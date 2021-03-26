package com.mahmoudkhalil.newsapp;

public class News {
    private String title;
    private String section;
    private String url;
    private String author;
    private String dateTime;

    public News(String title, String section, String url, String author, String dateTime) {
        this.title = title;
        this.section = section;
        this.url = url;
        this.author = author;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public String getDateTime() {
        return dateTime;
    }
}
