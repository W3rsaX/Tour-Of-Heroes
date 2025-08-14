package com.example.demo.E2ETests;

import com.example.demo.E2ETests.libs.Assertions;
import com.example.demo.E2ETests.libs.DataCreator;
import com.example.demo.E2ETests.libs.Responses;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("e2e")
public class HeroGetByIdE2ETest {

  @Test
  public void testGetHero() {
    Response responseSaveHero = Responses.responseSaveHero();

    Long id = responseSaveHero.jsonPath().getLong("id");

    Response responseGetHero = Responses.responseGetHero(id);

    String[] expectedFieldes = DataCreator.createExpectedHeraFieldesAll();
    Assertions.assertJsonHasFields(responseGetHero, expectedFieldes);
  }
}
