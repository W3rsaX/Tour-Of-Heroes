package com.example.demo.E2ETests.libs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;

public class Responses {

  public static Response responseSaveHero() {
    HashMap<String, String> hero = DataCreator.createDefaultHero();

    Response responseSaveHero = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .body(hero)
        .post("http://localhost:8080/hero/save")
        .andReturn();

    return responseSaveHero;
  }

  public static Response responseGetHero(Long id) {
    Response getNewHero = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .get("http://localhost:8080/hero/get/" + id)
        .andReturn();

    return getNewHero;
  }
}
