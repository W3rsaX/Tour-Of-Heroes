package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import com.example.demo.model.HeroDashboard;
import com.example.demo.service.HeroEditor;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.Hero;
import com.example.demo.service.HeroService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("http://localhost:4200")
public class HeroController {

	@Autowired
	private HeroService heroService;
	@Autowired
	private HeroEditor heroEditor;

	@PostMapping("/hero/save")
	public Hero saveHero(@RequestBody Hero hero) {
		hero = heroEditor.firstUpper(hero);
		hero = heroEditor.genderController(hero);
		return heroService.saveHero(hero);
	}

	@PostMapping("/hero/save/file")
	public void saveFile(@RequestBody MultipartFile file) throws IOException, JAXBException {
		switch (file.getContentType()){
			case ("text/csv"):
				heroService.batchSaveHeroCSV(file);
				break;
			case ("application/json"):
				heroService.batchSaveHeroJSON(file);
				break;
			case ("text/xml"):
				heroService.batchSaveHeroXML(file);
				break;
		}
	}

	@GetMapping("/hero/get/file/csv")
	public byte[] getCsv(@RequestParam("Like") String like) throws IOException {
		return heroService.getCsv(like);
	}

	@GetMapping("/hero/get/file/json")
	public byte[] getJson(@RequestParam("Like") String like) throws IOException {
		return heroService.getJson(like);
	}

	@GetMapping("/hero/get/file/xml")
	public byte[] getXml(@RequestParam("Like") String like) throws IOException, JAXBException {
		return heroService.getXml(like);
	}

	@GetMapping("/hero/get")
	public List<Hero> getHeroes(@RequestParam("Sort") String sort, @RequestParam("SortType") String sortType, @RequestParam("FilterValue") String filterValue, @RequestParam("PageSize") Integer pageSize, @RequestParam("PageIndex") Integer pageIndex) {
		return heroService.getHeroes(sort, sortType,filterValue, pageSize, pageIndex);
	}

	@GetMapping("/hero/get/length")
	public Long getLength(@RequestParam("Like") String like) {
		return heroService.getLength(like);
	}

	@GetMapping("/hero/getTop")
	public List<HeroDashboard> getTopHeroHuman(@RequestParam("Race") String race) throws IOException, ClassNotFoundException {
		return heroService.getTopHero(race);
	}

	@GetMapping("/hero/get/{heroId}")
	public Hero getHero(@PathVariable Long heroId) throws JsonProcessingException {
		return heroService.getHero(heroId);
	}

	@DeleteMapping("/hero/delete/{heroId}")
	public void deleteHero(@PathVariable Long heroId) {
		heroService.deleteHero(heroId);
	}

	@PutMapping("/hero/update")
	public Hero updateHero(@RequestBody Hero hero) {
		hero = heroEditor.firstUpper(hero);
		hero = heroEditor.genderController(hero);
		return heroService.updateHero(hero);
	}
}
