package com.example.demo.dao;

import com.example.demo.model.HeroJson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HeroJsonDao extends JpaRepository<HeroJson, Long> {

    List<HeroJson> findAllByOrderByIdAsc();

    List<HeroJson> findAllByNameIgnoreCaseContaining(String like);
}