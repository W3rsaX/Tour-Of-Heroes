package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Entity
@JsonIgnoreProperties("id")
@Table(name = "hero")
@Configuration
@EnableSpringDataWebSupport(
        pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
public class HeroJson {

    @Id
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    @Column(name = "name")
    private String name;

    @JsonProperty("gender")
    @Column(name = "gender")
    private String gender;

    @JsonProperty("power")
    @Column(name = "power")
    private int power;

    @JsonProperty("race")
    @Column(name = "race")
    private String race;

    public HeroJson(String name, String gender, int power, String race) {
        this.name = name;
        this.gender = gender;
        this.power = power;
        this.race = race;
    }

    public HeroJson() {

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {this.gender = gender; }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

}
