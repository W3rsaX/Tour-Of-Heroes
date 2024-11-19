package com.example.demo.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.dao.HeroDashboardDao;
import com.example.demo.dao.HeroJsonDao;
import com.example.demo.model.HeroDashboard;
import com.example.demo.model.HeroJson;
import com.example.demo.model.Heroes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.demo.dao.HeroDao;
import com.example.demo.model.Hero;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPooled;

@JsonIgnoreProperties(ignoreUnknown = true)
@Service
public class HeroService {

    @Autowired
    private HeroDao heroDao;

    @Autowired
    private HeroJsonDao heroJsonDao;

    @Autowired
    private HeroDashboardDao heroDashboardDao;

    int BATCH_SIZE = 1000;

    int TTL = 60;

    @Autowired
    FileParser fileParser;

    public Hero saveHero(Hero hero) {
        return heroDao.save(hero);
    }

    public List<Hero> getHeroes(String sort, String sortType, String filterValue, Integer pageSize, Integer pageIndex) {
        List<Hero> heroes = new ArrayList<>();
        if (sortType.equals("asc")) {
            heroes = heroDao.findAllByNameIgnoreCaseContaining(filterValue, PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.ASC, sort)));
        } else {
            heroes = heroDao.findAllByNameIgnoreCaseContaining(filterValue, PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, sort)));
        }
        return heroes;
    }

    public List<HeroDashboard> getTopHero(String race) throws IOException, ClassNotFoundException {
        JedisPooled jedis = new JedisPooled("localhost", 6379);
        List<HeroDashboard> heroes = new ArrayList<>();
        String key = "Top:%s".formatted(race);
        //Забираем данные из Redis
        if (jedis.get(key.getBytes()) != null) {
            ByteArrayInputStream bais = new ByteArrayInputStream(jedis.get(key.getBytes()));
            ObjectInputStream in = new ObjectInputStream(bais);
            heroes = (List<HeroDashboard>) in.readObject();
            in.close();
            return heroes;
        }
        //Данных нет в Redis обращаемся к БД
        heroes = heroDashboardDao.getTopHeroesByRace(race);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(heroes);
        jedis.setex(key.getBytes(), TTL, baos.toByteArray());
        out.close();
        return heroes;
    }

    public Hero getHero(Long heroId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JedisPooled jedis = new JedisPooled("localhost", 6379);
        String key = "Hero:%d".formatted(heroId);
        String str = jedis.get(key);
        if (str != null) {
            return mapper.readValue(str, Hero.class);
        }
        Hero hero = heroDao.findById(heroId).orElseThrow();
        jedis.setex(key, TTL, mapper.writeValueAsString(hero));
        return hero;
    }

    public Long getLength(String like) {
        return heroDao.countByNameIgnoreCaseContaining(like);
    }

    public void deleteHero(Long heroId) {
        heroDao.deleteById(heroId);
    }

    public Hero updateHero(Hero hero) {
        heroDao.findById(hero.getId()).orElseThrow();
        return heroDao.save(hero);
    }

    public void batchSaveHeroCSV(MultipartFile file) throws IOException {
        String line = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        int countBatch = 1;
        List<Hero> heroes = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            Hero hero = fileParser.convertCSV(line);
            if (hero == null) {
                countBatch += 1;
                continue;
            }
            heroes.add(hero);
            if (countBatch % BATCH_SIZE == 0) {
                heroDao.saveAll(heroes);
                countBatch = 0;
                heroes.clear();
            }
            countBatch += 1;
        }
        if (countBatch % BATCH_SIZE != 0) {
            heroDao.saveAll(heroes);
            heroes.clear();
        }
    }

    public void batchSaveHeroJSON(MultipartFile file) throws IOException {
        String json = new String(file.getInputStream().readAllBytes());
        var jacksonMapper = new ObjectMapper();
        List<Hero> heroes = jacksonMapper.readValue(json, new TypeReference<List<Hero>>() {
        });
        heroDao.saveAll(heroes);
        heroes.clear();
    }

    public void batchSaveHeroXML(MultipartFile file) throws IOException, JAXBException {
        String xml = new String(file.getInputStream().readAllBytes());
        Heroes heroes = fileParser.convertXML(xml);
        heroDao.saveAll(heroes.getHeroes());
        heroes.getHeroes().clear();
    }

    public byte[] getCsv(String like) throws IOException {
        List<Hero> heroes = heroDao.findAllByNameIgnoreCaseContaining(like);
        Path path = Path.of("HeroesCSV.csv");
        Files.deleteIfExists(path);
        Files.createFile(path);
        BufferedWriter writer = Files.newBufferedWriter(path);
        for (Hero hero : heroes) {
            String str = "\"" + hero.getName() + "\",\"" + hero.getGender() + "\"," + hero.getPower() + ",\"" + hero.getRace() + "\"\n";
            writer.append(str);
        }
        writer.close();
        return Files.readAllBytes(path);
    }

    public byte[] getJson(String like) throws IOException {
        Path path = Path.of("HeroesJSON.json");
        Files.deleteIfExists(path);
        Files.createFile(path);
        BufferedWriter writer = Files.newBufferedWriter(path);
        List<HeroJson> heroes = heroJsonDao.findAllByNameIgnoreCaseContaining(like);
        ObjectMapper jacksonMapper = new ObjectMapper();
        jacksonMapper.writeValue(writer,heroes);
        writer.close();
        return Files.readAllBytes(path);
    }

    public byte[] getXml(String like) throws IOException, JAXBException {
        Path path = Path.of("HeroesXML.xml");
        Files.deleteIfExists(path);
        Files.createFile(path);
        BufferedWriter writer = Files.newBufferedWriter(path);
        Heroes heroesClass = new Heroes();
        heroesClass.setHeroes(heroDao.findAllByNameIgnoreCaseContaining(like));
        JAXBContext jaxbContext = JAXBContext.newInstance(Heroes.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(heroesClass, writer);
        writer.close();
        return Files.readAllBytes(path);
    }
}
