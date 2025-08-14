package com.example.demo.E2ETests.libs;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class Assertions {

  public static void assertJsonByName(Response response, String name, String expectedAnswer) {
    response.then().assertThat().body("$", hasKey(name));

    String value = response.jsonPath().getString(name);
    assertEquals(expectedAnswer, value);
  }

  public static void assertJsonByNames(Response response, String[] name, String[] expectedAnswer) {
    for (int i = 0; i < name.length; i++) {
      Assertions.assertJsonByName(response, name[i], expectedAnswer[i]);
    }
  }

  public static void assertIsNull(Response response) {
    assertEquals("null", response.asString(), response.asString());
  }

  public static void assertJsonFiledIsNotNull(Response response, String name) {
    String value = response.jsonPath().getString(name);
    assertNotNull(value, "Поле '" + name + "' равно null");
  }

  public static void assertJsonFiledsIsNotNull(Response response, String[] names) {
    for (String name : names) {
      Assertions.assertJsonFiledIsNotNull(response, name);
    }
  }

  public static void assertResponseTextEquals(Response response, String expectedAnswer) {
    assertEquals(expectedAnswer,
        response.asString(),
        "Response text is not as expected");
  }

  public static void assertResponseCodeEquals(Response response, long expectedAnswer) {
    assertEquals(expectedAnswer,
        response.statusCode());
  }

  public static void assertJsonHasField(Response response, String expectedFieldName) {
    response.then().assertThat().body("$", hasKey(expectedFieldName));
  }

  public static void assertJsonHasFields(Response response, String[] expectedFieldNames) {
    for (String expectedFieldName : expectedFieldNames) {
      Assertions.assertJsonHasField(response, expectedFieldName);
    }
  }

  public static void assertJsonArrayHasField(Response response, String expectedFieldName) {
    response.then()
        .body("$", hasSize(greaterThan(0)))
        .body("[0]." + expectedFieldName, notNullValue());
  }

  public static void assertJsonArrayHasFields(Response response, String[] expectedFieldNames) {
    for (String expectedFieldName : expectedFieldNames) {
      Assertions.assertJsonArrayHasField(response, expectedFieldName);
    }
  }

  public static void assertJsonHasNotField(Response response, String unexpectedFieldName) {
    response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
  }
}
