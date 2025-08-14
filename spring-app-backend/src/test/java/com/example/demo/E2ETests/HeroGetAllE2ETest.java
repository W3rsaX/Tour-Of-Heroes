package com.example.demo.E2ETests;

import com.example.demo.E2ETests.libs.Assertions;
import com.example.demo.E2ETests.libs.DataCreator;
import com.example.demo.E2ETests.libs.Responses;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("e2e")
public class HeroGetAllE2ETest {

  @Test
  public void testGetAllHeroes(){
    Response responseSaveHero = Responses.responseSaveHero();

    HashMap<String, String> params = new HashMap<>();
    params.put("SortCol", "Name");
    params.put("SortType", "asc");
    params.put("FilterValue", "");
    params.put("PageSize", "100");
    params.put("PageIndex", "1");

    Response responseGetAllHeroes = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .params(params)
        .get("http://localhost:8080/hero/get")
        .andReturn();

    String[] expectedFields = DataCreator.createExpectedHeraFieldesAll();
    Assertions.assertResponseCodeEquals(responseGetAllHeroes, 200);
    Assertions.assertJsonArrayHasFields(responseGetAllHeroes, expectedFields);
    Assertions.assertJsonFiledsIsNotNull(responseGetAllHeroes, expectedFields);
  }
}
