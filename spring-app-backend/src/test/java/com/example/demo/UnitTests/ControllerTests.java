package com.example.demo.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.controller.HeroController;
import com.example.demo.model.Hero;
import com.example.demo.service.HeroEditor;
import com.example.demo.service.HeroService;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ControllerTests {

  @Mock
  private HeroEditor heroEditor;

  @Mock
  private HeroService heroService;

  @InjectMocks
  private HeroController heroController;

//  SAVE HERO
  @Test
  public void saveHero_Success() {
    Hero hero = new Hero("Erich", "M", 99, "Elf");

    when(heroEditor.firstUpper(hero)).thenReturn(hero);
    when(heroEditor.genderController(hero)).thenReturn(hero);

    ResponseEntity<?> response = heroController.saveHero(hero);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(hero, response.getBody());
  }

  @Test
  public void saveHero_Unsuccess() {
    Hero hero = new Hero("Erich", "M", 99, "Elf");

    when(heroEditor.firstUpper(hero)).thenReturn(hero);
    when(heroEditor.genderController(hero)).thenReturn(hero);
    doThrow(new RuntimeException("Service unavailable")).when(heroService).saveHero(hero);

    ResponseEntity<?> response = heroController.saveHero(hero);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNull(response.getBody());
  }

// SAVE HERO
  @Test
  public void saveFile_CSV_Success() throws IOException, JAXBException {
    MultipartFile file = new MockMultipartFile(
        "file",
        "heroes.csv",
        "text/csv",
        "name,gender,age,race\nErich,M,99,Elf".getBytes()
    );

    ResponseEntity<?> response = heroController.saveFile(file);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(heroService).batchSaveHeroCSV(file);
  }

  @Test
  public void saveFile_JSON_Success() throws IOException, JAXBException {
    MultipartFile file = new MockMultipartFile(
        "file",
        "heroes.json",
        "application/json",
        "[{\"name\":\"Erich\",\"gender\":\"M\",\"age\":99,\"race\":\"Elf\"}]".getBytes()
    );

    ResponseEntity<?> response = heroController.saveFile(file);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(heroService).batchSaveHeroJSON(file);
  }

  @Test
  public void saveFile_XML_Success() throws IOException, JAXBException {
    MultipartFile file = new MockMultipartFile(
        "file",
        "heroes.xml",
        "text/xml",
        "<heroes><hero><name>Erich</name><gender>M</gender><age>99</age><race>Elf</race></hero></heroes>".getBytes()
    );

    ResponseEntity<?> response = heroController.saveFile(file);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(heroService).batchSaveHeroXML(file);
  }

  @Test
  public void saveFile_UnsupportedType() throws IOException, JAXBException {
    MultipartFile file = new MockMultipartFile(
        "file",
        "heroes.txt",
        "text/plain",
        "Some text content".getBytes()
    );

    ResponseEntity<?> response = heroController.saveFile(file);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void saveFile_ServiceUnavailable() throws IOException, JAXBException {
    MultipartFile file = new MockMultipartFile(
        "file",
        "heroes.csv",
        "text/csv",
        "name,gender,age,race\nErich,M,99,Elf".getBytes()
    );

    doThrow(new IOException("Service error")).when(heroService).batchSaveHeroCSV(file);

    ResponseEntity<?> response = heroController.saveFile(file);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
  }

//  GET CSV
  @Test
  public void getCsv_Success() throws IOException {
    String csvData = "name,gender,age,race \nErich,M,99,Elf";
    when(heroService.getCsv("test")).thenReturn(csvData.getBytes());

    ResponseEntity<?> response = heroController.getCsv("test");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    verify(heroService).getCsv("test");
  }

  @Test
  public void getCsv_EmptyParam() throws IOException {
    String csvData = "name,gender,age,race\nErich,M,99,Elf";
    when(heroService.getCsv("")).thenReturn(csvData.getBytes());

    ResponseEntity<?> response = heroController.getCsv("");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    verify(heroService).getCsv("");
  }

  @Test
  public void getCsv_ServiceThrowsIOException() throws IOException {
    when(heroService.getCsv("error")).thenThrow(new IOException("File error"));

    ResponseEntity<?> response = heroController.getCsv("error");

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNull(response.getBody());
  }

//  GET JSON
  @Test
  public void getJson_Success() throws IOException {
    String jsonData = "[{\"name\":\"Erich\",\"gender\":\"M\",\"age\":99,\"race\":\"Elf\"}]";
    when(heroService.getJson("test")).thenReturn(jsonData.getBytes());

    ResponseEntity<?> response = heroController.getJson("test");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    verify(heroService).getJson("test");
  }

  @Test
  public void getJson_EmptyParam() throws IOException {
    String jsonData = "[{\"name\":\"Erich\",\"gender\":\"M\",\"age\":99,\"race\":\"Elf\"}]";
    when(heroService.getJson("")).thenReturn(jsonData.getBytes());

    ResponseEntity<?> response = heroController.getJson("");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
  }

  @Test
  public void getJson_IOException() throws IOException {
    when(heroService.getJson("error")).thenThrow(new IOException("File error"));

    ResponseEntity<?> response = heroController.getJson("error");

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNull(response.getBody());
  }

  //  GET XML
  @Test
  public void getXml_Success() throws IOException, JAXBException {
    String xmlData = "<heroes><hero><name>Erich</name><gender>M</gender><age>99</age><race>Elf</race></hero></heroes>";
    when(heroService.getXml("test")).thenReturn(xmlData.getBytes());

    ResponseEntity<?> response = heroController.getXml("test");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    verify(heroService).getXml("test");
  }

  @Test
  public void getXml_EmptyParam() throws IOException, JAXBException {
    String xmlData = "<heroes/>";
    when(heroService.getXml("")).thenReturn(xmlData.getBytes());

    ResponseEntity<?> response = heroController.getXml("");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
  }

  @Test
  public void getXml_IOException() throws IOException, JAXBException {
    when(heroService.getXml("io_error")).thenThrow(new IOException("XML file error"));

    ResponseEntity<?> response = heroController.getXml("io_error");

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  public void getXml_JAXBException() throws IOException, JAXBException {
    when(heroService.getXml("jaxb_error")).thenThrow(new JAXBException("XML parsing error"));

    ResponseEntity<?> response = heroController.getXml("jaxb_error");

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNull(response.getBody());
  }

//  DELETE HERO
  @Test
  public void deleteHero_Success() {
    Long heroId = 1L;

    ResponseEntity<?> response = heroController.deleteHero(heroId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(heroService).deleteHero(heroId);
  }

  @Test
  public void deleteHero_NotFound() {
    Long heroId = 1L;
    doThrow(new RuntimeException("Hero not found")).when(heroService).deleteHero(heroId);

    ResponseEntity<?> response = heroController.deleteHero(heroId);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
  }

  @Test
  public void deleteHero_ServiceThrowsException() {
    Long heroId = 1L;
    doThrow(new RuntimeException("Database error")).when(heroService).deleteHero(heroId);

    ResponseEntity<?> response = heroController.deleteHero(heroId);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
  }
//  UPDATE HERO
  @Test
  public void updateHero_Success() {
    Hero hero = new Hero("Erich", "M", 99, "Elf");

    when(heroEditor.firstUpper(hero)).thenReturn(hero);
    when(heroEditor.genderController(hero)).thenReturn(hero);

    ResponseEntity<?> response = heroController.updateHero(hero);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(heroEditor).firstUpper(hero);
    verify(heroEditor).genderController(hero);
    verify(heroService).updateHero(hero);
  }

  @Test
  public void updateHero_ThrowsException() {
    Hero hero = new Hero("erich", "m", 99, "elf");
    when(heroEditor.firstUpper(hero)).thenThrow(new RuntimeException("Editor error"));

    ResponseEntity<?> response = heroController.updateHero(hero);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
  }
}
