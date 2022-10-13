package com.example.newprojectmishanxx.Model;

import java.util.UUID;

public class Service {

    private static int idCounter= 0;
    private int id;
    private String name;

    public Service() {
//        this.id = UUID.randomUUID().toString();
    }

    public Service(int id, String name) {
        this.id = idCounter++;
        this.name = name;
    }

    public Service(String name) {
        this.id = idCounter++;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
