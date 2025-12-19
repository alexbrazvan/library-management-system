package com.library;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book {
    private IntegerProperty id;
    private StringProperty title;
    private StringProperty author;
    private BooleanProperty available;

    public Book(int id, String title, String author, boolean available) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.available = new SimpleBooleanProperty(available);
    }

    public int getBookId() {
        return id.get();
    }
    public IntegerProperty bookIdProperty() {
        return id;
    }
    public void setBookId(int id) {
        this.id.set(id);
    }
    public String getTitle(){
        return title.get();
    }
    public StringProperty titleProperty() {
        return title;
    }
    public void setTitle(String title) {
        this.title.set(title);
    }
    public String getAuthor(){
        return author.get();
    }
    public StringProperty authorProperty() {
        return author;
    }
    public void setAuthor(String author) {
        this.author.set(author);
    }
    public boolean isAvailable() {
        return available.get();
    }
    public BooleanProperty availableProperty() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available.set(available);
    }
    
}
