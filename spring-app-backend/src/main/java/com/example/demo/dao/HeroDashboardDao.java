package com.example.demo.dao;

import com.example.demo.model.HeroDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface HeroDashboardDao extends JpaRepository<HeroDashboard, Long> {
    @Query(value = "SELECT id,name,power FROM hero WHERE race = ?1 ORDER BY power DESC LIMIT 1000", nativeQuery = true)
    List<HeroDashboard> getTopHeroesByRace(String race);
}
