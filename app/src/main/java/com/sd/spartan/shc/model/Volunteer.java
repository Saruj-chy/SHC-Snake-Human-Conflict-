package com.sd.spartan.shc.model;

public class Volunteer {
   private final String id;
    private final String name_ban;
    private final String name_eng;
    private final String address_ban;
    private final String address_eng;
    private final String phone;

    public Volunteer(String id, String name_ban, String name_eng, String address_ban, String address_eng, String phone) {
        this.id = id;
        this.name_ban = name_ban;
        this.name_eng = name_eng;
        this.address_ban = address_ban;
        this.address_eng = address_eng;
        this.phone = phone;
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

    public String getAddress_ban() {
        return address_ban;
    }

    public String getAddress_eng() {
        return address_eng;
    }

    public String getPhone() {
        return phone;
    }
}
