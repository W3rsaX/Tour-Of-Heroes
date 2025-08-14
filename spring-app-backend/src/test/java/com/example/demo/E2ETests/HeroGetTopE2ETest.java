package com.example.demo.E2ETests;

import com.example.demo.E2ETests.libs.Assertions;
import com.example.demo.E2ETests.libs.DataCreator;
import com.example.demo.E2ETests.libs.Responses;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("e2e")
public class HeroGetTopE2ETest {

  @Test
  public void testGetTopHeroes() {
    Response responseSaveHero = Responses.responseSaveHero();

    Response responseGetTopHeroes = RestAssured
        .given()
        .param("Race", "elf")
        .contentType(ContentType.JSON)
        .get("http://localhost:8080/hero/getTop")
        .andReturn();

    String[] expectedFields = DataCreator.createExpectedHeraFieldesForTop();
    Assertions.assertResponseCodeEquals(responseGetTopHeroes, 200);
    Assertions.assertJsonArrayHasFields(responseGetTopHeroes, expectedFields);
    Assertions.assertJsonFiledsIsNotNull(responseGetTopHeroes, expectedFields);
  }
}
