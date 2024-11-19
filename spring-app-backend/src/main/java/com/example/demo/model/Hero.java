package com.example.demo.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@Entity
@XmlType(propOrder = {"name", "gender", "power", "race"})
@Table(name = "hero")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class Hero {

	@Id
	@SequenceGenerator(name = "sequence_id_auto_gen", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_id_auto_gen")
	private long id;

	@JsonProperty("name")
	@Column(name = "name")
	private String name;

	@JsonProperty("gender")
	@Column(name = "gender")
	private String gender;

	@JsonProperty("power")
	@Column(name = "power")
	private int power;

	@JsonProperty("race")

	@Column(name = "race")
	private String race;

	public Hero(String name, String gender, int power, String race) {
		this.name = name;
		this.gender = gender;
		this.power = power;
		this.race = race;
	}

	public Hero() {
		
	}

	public long getId() {
		return id;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name = "gender")
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	@XmlElement(name = "power")
	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}
	@XmlElement(name = "race")
	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

}
