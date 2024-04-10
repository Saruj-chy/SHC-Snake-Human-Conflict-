package com.sd.spartan.shc.model;

public class SnakeName {
   private final String id;
    private final String name_ban;
    private final String name_eng;
    private final String image ;


    public SnakeName(String id, String name_ban, String name_eng, String image) {
        this.id = id;
        this.name_ban = name_ban;
        this.name_eng = name_eng;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName_ban() {
        return name_ban;
    }

    public String getName_eng() {
        return name_eng;
    }

    public String getImage() {
        return image;
    }
}
