package com.example.demo.IntegrationsTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.dao.HeroDao;
import com.example.demo.dao.HeroDashboardDao;
import com.example.demo.model.Hero;
import com.example.demo.model.HeroDashboard;
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
public class HeroDashboardDaoIntegrationTests {

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
  private HeroDao heroDao;

  @Autowired
  private HeroDashboardDao heroDashboardDao;

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @BeforeEach
  void setUp() {
    heroDao.save(new Hero("Superman", "M", 95, "Human"));
    heroDao.save(new Hero("Batman", "M", 85, "Human"));
    heroDao.save(new Hero("Spider-Man", "M", 90, "Human"));
    heroDao.save(new Hero("Tristana", "M", 88, "Elf"));
    heroDao.save(new Hero("Balin", "M", 92, "Dwarf"));
  }

  @AfterEach
  void tearDown() {
    heroDashboardDao.deleteAll();
  }

  @Test
  void getTopHeroesByRace_shouldReturnOrderedByPowerDesc() {
    List<HeroDashboard> heroes = heroDashboardDao.getTopHeroesByRace("Human");

    assertThat(heroes)
        .hasSize(3)
        .extracting(HeroDashboard::getName)
        .containsExactly("Superman", "Spider-Man", "Batman");
  }

  @Test
  void getTopHeroesByRace_shouldReturnEmptyListForUnknownRace() {
    List<HeroDashboard> heroes = heroDashboardDao.getTopHeroesByRace("Unknown");

    assertThat(heroes).isEmpty();
  }

  @Test
  void getTopHeroesByRace_shouldLimitResults() {
    for (int i = 6; i <= 1005; i++) {
      heroDao.save(new Hero("Hero" + i, "M", 100, "Human"));
    }

    List<HeroDashboard> heroes = heroDashboardDao.getTopHeroesByRace("Human");

    assertThat(heroes).hasSize(1000);
  }
}
