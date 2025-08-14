package com.example.demo.E2ETests;


import com.example.demo.E2ETests.libs.Assertions;
import com.example.demo.E2ETests.libs.DataCreator;
import com.example.demo.E2ETests.libs.Responses;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("e2e")
public class HeroSaveE2ETest {

  @Test
  public void testSaveHero() {
    Response responseSaveHero = Responses.responseSaveHero();

    Assertions.assertResponseCodeEquals(responseSaveHero, 200);
    Assertions.assertJsonHasField(responseSaveHero, "id");
    Long id = Long.valueOf(responseSaveHero.jsonPath().getString("id"));

    String[] expectedFields = DataCreator.createExpectedHeraFieldesAll();

    Response responseGetHero = Responses.responseGetHero(id);
    Assertions.assertResponseCodeEquals(responseGetHero, 200);
    Assertions.assertJsonHasFields(responseGetHero, expectedFields);
  }
}
