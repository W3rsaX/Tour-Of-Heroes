package com.example.demo.IntegrationsTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.dao.HeroDao;
import com.example.demo.model.Hero;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class HeroDaoTests {

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
    heroDao.save(new Hero("supergirl", "Female", 92, "Dwarf"));
  }

  @AfterEach
  void tearDown() {
    heroDao.deleteAll();
  }

  @Test
  void findAllByNameIgnoreCaseContaining_withPagination_shouldReturnFilteredResults() {
    PageRequest pageRequest = PageRequest.of(0, 2);
    List<Hero> heroes = heroDao.findAllByNameIgnoreCaseContaining("man", pageRequest);

    assertThat(heroes).hasSize(2);
    assertThat(heroes).extracting(Hero::getName)
        .containsExactlyInAnyOrder("Superman", "Batman");
  }

  @Test
  void findAllByNameIgnoreCaseContaining_shouldReturnAllMatching() {
    List<Hero> heroes = heroDao.findAllByNameIgnoreCaseContaining("man");

    assertThat(heroes).hasSize(4);
    assertThat(heroes).extracting(Hero::getName)
        .containsExactlyInAnyOrder(
            "Superman", "Batman", "Spider-Man", "Iron Man"
        );
  }

  @Test
  void findAllByNameIgnoreCaseContaining_shouldBeCaseInsensitive() {
    List<Hero> heroes = heroDao.findAllByNameIgnoreCaseContaining("SUPER");

    assertThat(heroes).hasSize(2);
    assertThat(heroes).extracting(Hero::getName)
        .containsExactlyInAnyOrder("Superman", "supergirl");
  }

  @Test
  void findAllByNameIgnoreCaseContaining_shouldReturnEmptyListIfNoMatches() {
    List<Hero> heroes = heroDao.findAllByNameIgnoreCaseContaining("htfh");
    assertThat(heroes).isEmpty();
  }

  @Test
  void countByNameIgnoreCaseContaining_shouldReturnCorrectCount() {
    long count = heroDao.countByNameIgnoreCaseContaining("man");
    assertThat(count).isEqualTo(4);
  }

}
