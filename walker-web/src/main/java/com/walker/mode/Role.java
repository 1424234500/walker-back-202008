package com.walker.mode;

public class Role {

    String id;
    String name;
    String about;

    public String getId() {
        return id;
    }

    public Role setId(String id) {
        this.id = id;
        return this;
    }


    public String getName() {
        return name;
    }

    public Role setName(String name) {
        this.name = name;
        return this;
    }

    public String getAbout() {
        return about;
    }

    public Role setAbout(String about) {
        this.about = about;
        return this;
    }
}
