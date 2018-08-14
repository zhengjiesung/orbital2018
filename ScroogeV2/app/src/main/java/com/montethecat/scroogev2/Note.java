package com.montethecat.scroogev2;

public class Note {

    private String title;
    private String id;
    private String message;

    public Note(){}

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}

