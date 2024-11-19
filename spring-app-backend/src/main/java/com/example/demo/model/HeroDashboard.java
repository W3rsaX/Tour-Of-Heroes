package com.example.demo.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "hero")
public class HeroDashboard implements Serializable {
    @Id
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "power")
    private int power;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public HeroDashboard() {

    }

    public HeroDashboard(long id, String name, int power) {
        this.id = id;
        this.name = name;
        this.power = power;
    }
}
