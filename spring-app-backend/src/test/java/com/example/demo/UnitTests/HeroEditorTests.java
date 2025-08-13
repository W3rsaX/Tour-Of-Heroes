package com.example.demo.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.model.Hero;
import com.example.demo.service.HeroEditor;
import org.junit.jupiter.api.Test;

public class HeroEditorTests {

  private final HeroEditor heroEditor = new HeroEditor();

  @Test
  public void firstUpper_ShouldUpperFirstLetter() {
    Hero hero = new Hero("erich", "M", 1, "Human");

    Hero result = heroEditor.firstUpper(hero);

    assertEquals("Erich", result.getName());
    assertEquals("M", result.getGender());
    assertEquals(1, result.getPower());
    assertEquals("Human", result.getRace());
  }

  @Test
  public void firstUpper_ShouldNotChangeAlreadyUpper() {
    Hero hero = new Hero("erich", "M", 1, "Human");

    Hero result = heroEditor.firstUpper(hero);

    assertEquals("Erich", result.getName());
  }

  @Test
  public void firstUpper_ShouldUpSingleLetter() {
    Hero hero = new Hero("e", "M", 1, "Human");

    Hero result = heroEditor.firstUpper(hero);

    assertEquals("E", result.getName());
  }

  @Test
  public void genderController_ShouldSetEmptyGender() {
    Hero hero = new Hero("Alice", "", 80, "Human");

    Hero result = heroEditor.genderController(hero);

    assertEquals("None", result.getGender());
  }

  @Test
  public void genderController_ShouldSaveEmptyGender() {
    Hero hero = new Hero("Bob", "M", 90, "Orc");

    Hero result = heroEditor.genderController(hero);

    assertEquals("M", result.getGender());
  }
}
