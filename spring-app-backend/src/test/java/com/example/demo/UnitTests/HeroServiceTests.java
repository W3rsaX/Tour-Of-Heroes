package com.example.demo.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dao.HeroDao;
import com.example.demo.dao.HeroDashboardDao;
import com.example.demo.dao.HeroJsonDao;
import com.example.demo.model.Hero;
import com.example.demo.model.HeroDashboard;
import com.example.demo.model.HeroJson;
import com.example.demo.service.FileParser;
import com.example.demo.service.HeroService;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisConnectionException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HeroServiceTests {
  @Mock
  private HeroDao heroDao;

  @Mock
  private HeroJsonDao heroJsonDao;

  @Mock
  private HeroDashboardDao heroDashboardDao;

  @Mock
  private JedisPooled jedis;

  @Mock
  private FileParser fileParser;

  @InjectMocks
  private HeroService heroService;

  private Hero createTestHero(String name) {
    return new Hero(name, "m", 100, "elf");
  }

  private HeroDashboard createTestHero(long id, String name, int power) {
    return new HeroDashboard(id, name, power);
  }

  @Test
  void getHeroes_shouldReturnSortedAsc() throws Exception {
    List<Hero> expectedHeroes = new ArrayList<>();
    expectedHeroes.add(createTestHero("Hero1"));
    expectedHeroes.add(createTestHero("Hero2"));

    when(heroDao.findAllByNameIgnoreCaseContaining(anyString(),
        eq(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name")))))
        .thenReturn(expectedHeroes);

    CompletableFuture<ResponseEntity<?>> future = heroService.getHeroes("name", "asc", "hero", 10, 0);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedHeroes, response.getBody());
  }

  @Test
  void getHeroes_shouldReturnSortedDesc() throws Exception {
    List<Hero> expectedHeroes = new ArrayList<>();
    expectedHeroes.add(createTestHero("Hero2"));
    expectedHeroes.add(createTestHero("Hero1"));

    when(heroDao.findAllByNameIgnoreCaseContaining(anyString(),
        eq(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name")))))
        .thenReturn(expectedHeroes);

    CompletableFuture<ResponseEntity<?>> future = heroService.getHeroes("name", "desc", "hero", 10, 0);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedHeroes, response.getBody());
  }

  @Test
  void getHeroes_shouldHandleException() throws Exception {
    when(heroDao.findAllByNameIgnoreCaseContaining(anyString(), any()))
        .thenThrow(new RuntimeException());

    CompletableFuture<ResponseEntity<?>> future = heroService.getHeroes("name", "asc", "hero", 10, 0);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
  }

  @Test
  void getTopHero_shouldFetchFromDBAndCacheWhenNotInCache() throws Exception {
    String race = "Elf";
    String key = "Top:" + race;
    List<HeroDashboard> dbHeroes = List.of(
        createTestHero(1L, "Hero1", 100),
        createTestHero(2L, "Hero2", 100)
    );

    when(jedis.get(key.getBytes())).thenReturn(null);
    when(heroDashboardDao.getTopHeroesByRace(race)).thenReturn(dbHeroes);

    CompletableFuture<ResponseEntity<?>> future = heroService.getTopHero(race);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dbHeroes, response.getBody());
  }

  @Test
  void getTopHero_shouldHandleRedisConnectionError() throws Exception {
    String race = "Elf";
    List<HeroDashboard> dbHeroes = List.of(
        createTestHero(1L, "Hero1", 100),
        createTestHero(2L, "Hero2", 100)
    );

    when(jedis.get(any(byte[].class))).thenThrow(new JedisConnectionException("Connection failed"));
    when(heroDashboardDao.getTopHeroesByRace(race)).thenReturn(dbHeroes);

    CompletableFuture<ResponseEntity<?>> future = heroService.getTopHero(race);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dbHeroes, response.getBody());
  }

  @Test
  void getTopHero_shouldReturnServiceUnavailableOnGeneralException() throws Exception {
    String race = "Elf";

    when(jedis.get(any(byte[].class))).thenThrow(new RuntimeException("Unexpected error"));
    when(heroDashboardDao.getTopHeroesByRace(race)).thenThrow(new RuntimeException("Unexpected error"));

    CompletableFuture<ResponseEntity<?>> future = heroService.getTopHero(race);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
  }

  @Test
  void getLength_shouldReturnCountAndOkStatus_whenDaoReturnsSuccessfully() throws ExecutionException, InterruptedException {
    String searchString = "man";
    long expectedCount = 5L;
    when(heroDao.countByNameIgnoreCaseContaining(searchString)).thenReturn(expectedCount);

    CompletableFuture<ResponseEntity<?>> future = heroService.getLength(searchString);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedCount, response.getBody());
    verify(heroDao).countByNameIgnoreCaseContaining(searchString);
  }

  @Test
  void getLength_shouldReturnServiceUnavailable_whenDaoThrowsException() throws ExecutionException, InterruptedException {
    String searchString = "man";
    when(heroDao.countByNameIgnoreCaseContaining(searchString))
        .thenThrow(new RuntimeException("Database error"));

    CompletableFuture<ResponseEntity<?>> future = heroService.getLength(searchString);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNull(response.getBody());
    verify(heroDao).countByNameIgnoreCaseContaining(searchString);
  }

  @Test
  void getLength_shouldHandleEmptySearchString() throws ExecutionException, InterruptedException {
    String emptyString = "";
    long expectedCount = 10L;
    when(heroDao.countByNameIgnoreCaseContaining(emptyString)).thenReturn(expectedCount);

    CompletableFuture<ResponseEntity<?>> future = heroService.getLength(emptyString);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedCount, response.getBody());
    verify(heroDao).countByNameIgnoreCaseContaining(emptyString);
  }

  @Test
  void getLength_shouldHandleNullSearchString()
      throws ExecutionException, InterruptedException, ExecutionException {
    long expectedCount = 0L;
    when(heroDao.countByNameIgnoreCaseContaining(null)).thenReturn(expectedCount);

    CompletableFuture<ResponseEntity<?>> future = heroService.getLength(null);
    ResponseEntity<?> response = future.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedCount, response.getBody());
    verify(heroDao).countByNameIgnoreCaseContaining(null);
  }

  @Test
  void getCsv_ReturnsCorrectCsvData() throws IOException {
    List<Hero> heroes = Arrays.asList(
        new Hero("Arthur", "M", 90, "Human"),
        new Hero("Legolas", "M", 95, "Elf"),
        new Hero("Gimli", "M", 85, "Dwarf")
    );
    when(heroDao.findAllByNameIgnoreCaseContaining("test")).thenReturn(heroes);

    byte[] result = heroService.getCsv("test");
    String csvContent = new String(result, StandardCharsets.UTF_8);

    String[] lines = csvContent.split("\r?\n");
    assertEquals(4, lines.length);
    assertEquals("\"Name\",\"Gender\",\"Power\",\"Race\"", lines[0]);
    assertTrue(lines[1].contains("\"Arthur\",\"M\",\"90\",\"Human\""));
    assertTrue(lines[2].contains("\"Legolas\",\"M\",\"95\",\"Elf\""));
    assertTrue(lines[3].contains("\"Gimli\",\"M\",\"85\",\"Dwarf\""));
  }

  @Test
  void getCsv_ReturnsEmptyCsvWhenNoHeroesFound() throws IOException {
    when(heroDao.findAllByNameIgnoreCaseContaining("empty")).thenReturn(List.of());

    byte[] result = heroService.getCsv("empty");
    String csvContent = new String(result, StandardCharsets.UTF_8);

    String[] lines = csvContent.split("\r?\n");
    assertEquals(1, lines.length);
    assertEquals("\"Name\",\"Gender\",\"Power\",\"Race\"", lines[0]);
  }

  @Test
  void getJson_ReturnsCorrectJsonData() throws IOException {
    List<HeroJson> heroes = Arrays.asList(
        new HeroJson("Arthur", "M", 90, "Human"),
        new HeroJson("Legolas", "M", 95, "Elf")
    );
    when(heroJsonDao.findAllByNameIgnoreCaseContaining("test")).thenReturn(heroes);

    byte[] result = heroService.getJson("test");
    String jsonContent = new String(result, StandardCharsets.UTF_8);

    assertTrue(jsonContent.contains("\"name\":\"Arthur\""));
    assertTrue(jsonContent.contains("\"gender\":\"M\""));
    assertTrue(jsonContent.contains("\"power\":90"));
    assertTrue(jsonContent.contains("\"race\":\"Human\""));
    assertTrue(jsonContent.contains("\"name\":\"Legolas\""));
    assertTrue(jsonContent.contains("\"race\":\"Elf\""));
  }

  @Test
  void getJson_ReturnsEmptyArrayWhenNoHeroesFound() throws IOException {
    when(heroJsonDao.findAllByNameIgnoreCaseContaining("empty")).thenReturn(List.of());

    byte[] result = heroService.getJson("empty");
    String jsonContent = new String(result, StandardCharsets.UTF_8);

    assertEquals("[]", jsonContent);
  }

  @Test
  void getXml_ReturnsCorrectXmlData() throws IOException, JAXBException {
    List<Hero> heroes = Arrays.asList(
        new Hero("Arthur", "M", 90, "Human"),
        new Hero("Legolas", "M", 95, "Elf")
    );
    when(heroDao.findAllByNameIgnoreCaseContaining("test")).thenReturn(heroes);

    byte[] result = heroService.getXml("test");
    String xmlContent = new String(result);

    assertTrue(xmlContent.contains("<heroes>"));
    assertTrue(xmlContent.contains("<hero>"));
    assertTrue(xmlContent.contains("<name>Arthur</name>"));
    assertTrue(xmlContent.contains("<gender>M</gender>"));
    assertTrue(xmlContent.contains("<power>90</power>"));
    assertTrue(xmlContent.contains("<race>Human</race>"));
    assertTrue(xmlContent.contains("<name>Legolas</name>"));
    assertTrue(xmlContent.contains("<race>Elf</race>"));
    assertTrue(xmlContent.contains("</heroes>"));
  }

  @Test
  void getXml_ReturnsEmptyHeroesTagWhenNoHeroesFound() throws IOException, JAXBException {
    when(heroDao.findAllByNameIgnoreCaseContaining("empty")).thenReturn(List.of());

    byte[] result = heroService.getXml("empty");
    String xmlContent = new String(result);

    assertEquals("<heroes/>", xmlContent.trim());
  }
}
