package com.example.demo.E2ETests.libs;

import java.util.HashMap;

public class DataCreator {

  public static HashMap<String, String> createDefaultHero() {
    HashMap<String, String> hero = new HashMap<>();
    hero.put("name", "Erich");
    hero.put("gender", "М");
    hero.put("power", "99");
    hero.put("race", "elf");
    return hero;
  }

  public static String[] createExpectedHeraFieldesAll() {
    return new String[] {"id", "name", "gender", "power", "race"};
  }

  public static String[] createExpectedHeraFieldesForTop() {
    return new String[] {"id", "name", "power"};
  }
}
