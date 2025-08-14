package com.example.demo.IntegrationsTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.dao.HeroDao;
import com.example.demo.dao.HeroJsonDao;
import com.example.demo.model.Hero;
import com.example.demo.model.HeroJson;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class HeroJsonDaoIntegrationTests {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private HeroJsonDao heroJsonDao;

  @Autowired
  private HeroDao heroDao;

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @BeforeEach
  void setUp() {
    heroDao.save(new Hero("Superman", "Male", 95, "Dwarf"));
    heroDao.save(new Hero("Batman", "Male", 85, "Human"));
    heroDao.save(new Hero("Spider-Man", "Male", 90, "Dwarf"));
    heroDao.save(new Hero("Iron Man", "Male", 88, "Human"));
  }

  @AfterEach
  void cleanup() {
    heroDao.deleteAll();
  }

  @Test
  void findAllByOrderByIdAsc_shouldReturnHeroesInOrder() {
    List<HeroJson> heroes = heroJsonDao.findAllByOrderByIdAsc();

    assertThat(heroes)
        .hasSize(4)
        .extracting(HeroJson::getName)
        .containsExactly("Superman", "Batman", "Spider-Man", "Iron Man");
  }

  @Test
  void findAllByNameIgnoreCaseContaining_shouldFindMatchingHeroes() {
    List<HeroJson> heroes = heroJsonDao.findAllByNameIgnoreCaseContaining("S");

    assertThat(heroes)
        .hasSize(2)
        .extracting(HeroJson::getName)
        .containsExactlyInAnyOrder("Superman", "Spider-Man");
  }

  @Test
  void findAllByNameIgnoreCaseContaining_shouldBeCaseInsensitive() {
    List<HeroJson> heroes = heroJsonDao.findAllByNameIgnoreCaseContaining("SUPER");

    assertThat(heroes)
        .hasSize(1)
        .extracting(HeroJson::getName)
        .containsExactly("Superman");
  }

  @Test
  void findAllByNameIgnoreCaseContaining_shouldReturnEmptyListForNoMatches() {
    List<HeroJson> heroes = heroJsonDao.findAllByNameIgnoreCaseContaining("Hulk");

    assertThat(heroes).isEmpty();
  }
}
