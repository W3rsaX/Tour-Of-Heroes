package com.example.demo.controller;

import com.example.demo.model.Hero;
import com.example.demo.service.HeroEditor;
import com.example.demo.service.HeroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("http://localhost:4200")
public class HeroController {

  @Autowired
  private HeroService heroService;
  @Autowired
  private HeroEditor heroEditor;

  @PostMapping("/hero/save")
  public ResponseEntity<?> saveHero(@RequestBody Hero hero) {
    try {
      hero = heroEditor.firstUpper(hero);
      hero = heroEditor.genderController(hero);
      heroService.saveHero(hero);
      return new ResponseEntity<>(hero, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

  }

  @PostMapping("/hero/save/file")
  public ResponseEntity<?> saveFile(@RequestBody MultipartFile file)
      throws IOException, JAXBException {
    try {
      switch (file.getContentType()) {
        case ("text/csv"):
          heroService.batchSaveHeroCSV(file);
          break;
        case ("application/json"):
          heroService.batchSaveHeroJSON(file);
          break;
        case ("text/xml"):
          heroService.batchSaveHeroXML(file);
          break;
        default:
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

  }

  @GetMapping("/hero/get/file/csv")
  public ResponseEntity<?> getCsv(@RequestParam("Like") String like) throws IOException {
    try {
      return new ResponseEntity<>(heroService.getCsv(like), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  @GetMapping("/hero/get/file/json")
  public ResponseEntity<?> getJson(@RequestParam("Like") String like) throws IOException {
    try {
      return new ResponseEntity<>(heroService.getJson(like), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

  }

  @GetMapping("/hero/get/file/xml")
  public ResponseEntity<?> getXml(@RequestParam("Like") String like)
      throws IOException, JAXBException {
    try {
      return new ResponseEntity<>(heroService.getXml(like), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  @GetMapping("/hero/get")
  public CompletableFuture<ResponseEntity<?>> getHeroes(@RequestParam("SortCol") String sortCol,
      @RequestParam("SortType") String sortType, @RequestParam("FilterValue") String filterValue,
      @RequestParam("PageSize") Integer pageSize, @RequestParam("PageIndex") Integer pageIndex) {
    return heroService.getHeroes(sortCol, sortType, filterValue, pageSize, pageIndex);
  }

  @GetMapping("/hero/get/length")
  public CompletableFuture<ResponseEntity<?>> getLength(@RequestParam("Like") String like) {
    return heroService.getLength(like);
  }

  @GetMapping("/hero/getTop")
  public CompletableFuture<ResponseEntity<?>> getTopHeroHuman(@RequestParam("Race") String race)
      throws IOException, ClassNotFoundException {
    return heroService.getTopHero(race);
  }

  @GetMapping("/hero/get/{heroId}")
  public CompletableFuture<ResponseEntity<?>> getHero(@PathVariable Long heroId)
      throws JsonProcessingException {
    return heroService.getHero(heroId);
  }

  @DeleteMapping("/hero/delete/{heroId}")
  public ResponseEntity<?> deleteHero(@PathVariable Long heroId) {
    try {
      heroService.deleteHero(heroId);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  @PutMapping("/hero/update")
  public ResponseEntity<?> updateHero(@RequestBody Hero hero) {
    try {
      hero = heroEditor.firstUpper(hero);
      hero = heroEditor.genderController(hero);
      heroService.updateHero(hero);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
  }
}
