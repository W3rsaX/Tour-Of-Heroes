package com.example.demo.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Hero;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HeroDao extends JpaRepository<Hero, Long> {

  List<Hero> findAllByNameIgnoreCaseContaining(String like, PageRequest pageRequest);

  List<Hero> findAllByNameIgnoreCaseContaining(String like);

  long countByNameIgnoreCaseContaining(String like);
}
