package com.example.demo.E2ETests;

import com.example.demo.E2ETests.libs.Assertions;
import com.example.demo.E2ETests.libs.Responses;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


public class HeroDeleteTest {

  @Test
  public void testHeroDelete() {
    Response responseSaveHero = Responses.responseSaveHero();

    Long id = responseSaveHero.jsonPath().getLong("id");

    Response responseGetHero = Responses.responseGetHero(id);
    Assertions.assertResponseCodeEquals(responseGetHero, 200);

    Response responseDeleteHero = RestAssured
        .delete("http://localhost:8080/hero/delete/" + id)
        .andReturn();

    responseGetHero = Responses.responseGetHero(id);
    Assertions.assertIsNull(responseGetHero);
  }
}
