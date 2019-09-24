package com.walker.mode;

public class WebUser {

    String id;
    String pwd;
    String name;
    String about;


    public String getId() {
        return id;
    }

    public WebUser setId(String id) {
        this.id = id;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public WebUser setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public String getName() {
        return name;
    }

    public WebUser setName(String name) {
        this.name = name;
        return this;
    }

    public String getAbout() {
        return about;
    }

    public WebUser setAbout(String about) {
        this.about = about;
        return this;
    }
}
