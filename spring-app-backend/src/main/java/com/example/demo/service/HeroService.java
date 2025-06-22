package com.example.demo.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.demo.dao.HeroDashboardDao;
import com.example.demo.dao.HeroJsonDao;
import com.example.demo.model.HeroDashboard;
import com.example.demo.model.HeroJson;
import com.example.demo.model.Heroes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.demo.dao.HeroDao;
import com.example.demo.model.Hero;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisConnectionException;

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

    @Transactional
    public Hero saveHero(Hero hero) {
        return heroDao.save(hero);
    }

    @Async
    public CompletableFuture<List<Hero>> getHeroes(String sort, String sortType, String filterValue, Integer pageSize, Integer pageIndex) {
        List<Hero> heroes = new ArrayList<>();

        if (sortType.equals("asc")) {
            heroes = heroDao.findAllByNameIgnoreCaseContaining(filterValue, PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.ASC, sort)));
        } else {
            heroes = heroDao.findAllByNameIgnoreCaseContaining(filterValue, PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, sort)));
        }

        return CompletableFuture.completedFuture(heroes);
    }

    @Async
    public CompletableFuture<List<HeroDashboard>> getTopHero(String race) throws IOException, ClassNotFoundException {
        final String key = "Top:%s".formatted(race);
        List<HeroDashboard> heroes = new ArrayList<>();

        try (JedisPooled jedis = new JedisPooled("localhost", 6379)) {
            byte[] cachedHeroes = jedis.get(key.getBytes());
            if (cachedHeroes != null) {
                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(cachedHeroes))) {
                    return CompletableFuture.completedFuture((List<HeroDashboard>) in.readObject());
                }
            }

            heroes = heroDashboardDao.getTopHeroesByRace(race);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream out = new ObjectOutputStream(baos)) {
                out.writeObject(heroes);
                jedis.setex(key.getBytes(), TTL, baos.toByteArray());
            }
        } catch (JedisConnectionException e) {
            System.err.println("Redis connection error : " + e.getMessage());
        }

        if (heroes.isEmpty())
            heroes = heroDashboardDao.getTopHeroesByRace(race);

        return CompletableFuture.completedFuture(heroes);
    }

    @Async
    public CompletableFuture<Hero> getHero(Long heroId) throws JsonProcessingException {
        final String key = "Hero:%d".formatted(heroId);
        Hero hero = new Hero();

        try (JedisPooled jedis = new JedisPooled("localhost", 6379)) {
            String str = jedis.get(key);
            ObjectMapper mapper = new ObjectMapper();

            if (str != null)
                return CompletableFuture.completedFuture(mapper.readValue(str, Hero.class));

            hero = heroDao.findById(heroId).orElseThrow();
            jedis.setex(key, TTL, mapper.writeValueAsString(hero));
        } catch (JedisConnectionException e) {
            System.err.println("Redis connection error : " + e.getMessage());
        }

        if (hero == null)
            hero = heroDao.findById(heroId).orElseThrow();

        return CompletableFuture.completedFuture(hero);
    }

    @Async
    public CompletableFuture<Long> getLength(String like) {
        return CompletableFuture.completedFuture(heroDao.countByNameIgnoreCaseContaining(like));
    }

    @Transactional
    public void deleteHero(Long heroId) {
        heroDao.deleteById(heroId);
    }

    @Transactional
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

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            String[] header = {"Name", "Gender", "Power", "Race"};
            csvWriter.writeNext(header);

            for (Hero hero : heroes) {
                String[] record = {
                        hero.getName(),
                        hero.getGender(),
                        String.valueOf(hero.getPower()),
                        hero.getRace()
                };
                csvWriter.writeNext(record);
            }

            csvWriter.flush();
            return outputStream.toByteArray();
        }
    }

    public byte[] getJson(String like) throws IOException {
        List<HeroJson> heroes = heroJsonDao.findAllByNameIgnoreCaseContaining(like);
        ObjectMapper jacksonMapper = new ObjectMapper();
        return jacksonMapper.writeValueAsBytes(heroes);
    }

    public byte[] getXml(String like) throws IOException, JAXBException {
        List<Hero> heroes = heroDao.findAllByNameIgnoreCaseContaining(like);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Heroes heroesClass = new Heroes();
            heroesClass.setHeroes(heroes);

            JAXBContext jaxbContext = JAXBContext.newInstance(Heroes.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            marshaller.marshal(heroesClass, outputStream);

            return outputStream.toByteArray();
        }
    }
}
