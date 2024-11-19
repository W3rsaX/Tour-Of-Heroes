package com.example.demo.service;

import org.springframework.stereotype.Controller;
import com.example.demo.model.Hero;

@Controller
public class HeroEditor {

	public Hero firstUpper(Hero hero) {
		String capitalized = hero.getName();
		capitalized = capitalized.substring(0, 1).toUpperCase() + capitalized.substring(1);
		hero.setName(capitalized);
		return hero;
	}

	public Hero genderController(Hero hero) {
		String gender = hero.getGender();
		if (gender.isEmpty()) {
			gender = "None";
		}
		hero.setGender(gender);
		return hero;
	}
}
