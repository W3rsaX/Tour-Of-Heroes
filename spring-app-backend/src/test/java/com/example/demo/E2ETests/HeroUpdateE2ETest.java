package com.example.demo.E2ETests;

import com.example.demo.E2ETests.libs.Assertions;
import com.example.demo.E2ETests.libs.DataCreator;
import com.example.demo.E2ETests.libs.Responses;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

@Tag("e2e")
public class HeroUpdateE2ETest {

  @Test
  public void testUpdateHero() {
    Response responseSaveHero = Responses.responseSaveHero();

    Long id = Long.valueOf(responseSaveHero.jsonPath().getString("id"));

    HashMap<String, String> updateHero = DataCreator.createDefaultHero();
    String newName = "Elapchio";

    updateHero.put("id", id.toString());
    updateHero.put("name", newName);

    Response responseUpdateHero = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .body(updateHero)
        .put("http://localhost:8080/hero/update")
        .andReturn();

    Response getNewHero = Responses.responseGetHero(id);

    Assertions.assertJsonByName(getNewHero, "name", newName);
  }
}
