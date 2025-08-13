package com.example.demo.service;

import com.example.demo.model.Hero;
import com.example.demo.model.Heroes;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Controller;
import java.io.StringReader;

@Controller
public class FileParser {

  public Hero convertCSV(String csv) {
    try {
      csv = csv.replace("\"", "");
      String[] sSplit = csv.split("[.,:;]");
      Hero hero = new Hero();
      if (!sSplit[0].equals("name") && !sSplit[0].equals("")) {
        hero = new Hero(sSplit[0], sSplit[1], Integer.parseInt(sSplit[2]), sSplit[3]);
      } else {
        return null;
      }
      return hero;
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      return null;
    }
  }

  public Heroes convertXML(String xml) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(Heroes.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    return (Heroes) jaxbUnmarshaller.unmarshal(new StringReader(xml));
  }
}
