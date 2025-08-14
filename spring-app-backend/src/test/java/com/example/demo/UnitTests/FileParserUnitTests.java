package com.example.demo.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.model.Hero;
import com.example.demo.model.Heroes;
import com.example.demo.service.FileParser;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class FileParserUnitTests {

  private FileParser fileParser = new FileParser();

  @Test
  public void convertCSV_ValidInput() {
    String csv = "\"Erich\",\"M\",10,\"Elf\"";

    Hero result = fileParser.convertCSV(csv);

    assertEquals("Erich", result.getName());
    assertEquals("M", result.getGender());
    assertEquals(10, result.getPower());
    assertEquals("Elf", result.getRace());
  }

  @Test
  public void convertCSV_WithDifferentDelimiters() {
    String csv = "\"Erich\".\"M\":\"10\";\"Elf\"";

    Hero result = fileParser.convertCSV(csv);

    assertEquals("Erich", result.getName());
    assertEquals("M", result.getGender());
    assertEquals(10, result.getPower());
    assertEquals("Elf", result.getRace());
  }

  @Test
  public void convertCSV_HeaderRowReturnsNull() {
    String csv = "\"name\",\"gender\",\"power\",\"race\"";

    Hero result = fileParser.convertCSV(csv);

    assertNull(result);
  }

  @Test
  public void convertCSV_EmptyInput() {
    String csv = "";

    Hero result = fileParser.convertCSV(csv);

    assertNull(result);
  }

  @Test
  public void convertXML_ValidInput() throws JAXBException, JAXBException {
    String xml = "<heroes><hero><name>Erich</name><gender>M</gender><power>1</power><race>Elf</race></hero></heroes>";

    Heroes result = fileParser.convertXML(xml);

    assertNotNull(result);
    assertEquals(1, result.getHeroes().size());
    assertEquals("Erich", result.getHeroes().get(0).getName());
    assertEquals("M", result.getHeroes().get(0).getGender());
    assertEquals(1, result.getHeroes().get(0).getPower());
    assertEquals("Elf", result.getHeroes().get(0).getRace());
  }

  @Test
  public void convertXML_MultipleHeroes() throws JAXBException {
    String xml = "<heroes>" +
        "<hero><name>Erich</name><gender>M</gender><power>99</power><race>Dwarf</race></hero>" +
        "<hero><name>Erico</name><gender>F</gender><power>1</power><race>Elf</race></hero>" +
        "</heroes>";

    Heroes result = fileParser.convertXML(xml);

    assertNotNull(result);
    assertEquals(2, result.getHeroes().size());
  }

  @Test
  public void convertXML_EmptyHeroes() throws JAXBException {
    String xml = "<heroes></heroes>";

    Heroes result = fileParser.convertXML(xml);

    assertNotNull(result);
    assertTrue(result.getHeroes().isEmpty());
  }

  @Test
  public void convertXML_InvalidXMLFormat() {
    String xml = "<heroes><hero>Malformed XML</heroes>";

    assertThrows(JAXBException.class, () -> {
      fileParser.convertXML(xml);
    });
  }

  @Test
  public void convertXML_EmptyString() {
    assertThrows(JAXBException.class, () -> {
      fileParser.convertXML("");
    });
  }

  @Test
  public void convertXML_MissingFields() throws JAXBException {
    String xml = "<heroes><hero><name>John</name><power>30</power></hero></heroes>";

    Heroes result = fileParser.convertXML(xml);

    assertNotNull(result);
    assertEquals("John", result.getHeroes().get(0).getName());
    assertNull(result.getHeroes().get(0).getGender());
    assertEquals(30, result.getHeroes().get(0).getPower());
    assertNull(result.getHeroes().get(0).getRace());
  }
}
