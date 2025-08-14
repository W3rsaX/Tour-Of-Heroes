package com.example.demo.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "heroes")
public class Heroes {

  List<Hero> heroes = new ArrayList<>();

  @XmlElement(name = "hero")
  public List<Hero> getHeroes() {
    return heroes;
  }

  public void setHeroes(List<Hero> heroes) {
    this.heroes = heroes;
  }
}
