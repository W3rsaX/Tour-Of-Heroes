import { Component, OnInit } from '@angular/core';
import { Hero } from '../hero.model';
import { HeroService } from '../hero.service';
import { HeroDashboard } from '../hero-dashboard.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  heroHuman: HeroDashboard[] = [];
  heroDwarf: HeroDashboard[] = [];
  heroElf: HeroDashboard[] = [];

  constructor(private heroService: HeroService) { }

  ngOnInit(): void {
    this.getTopHeroes();
  }

  getTopHeroes(): void {

    this.heroService.getTopHeroByRace("Human").subscribe({
      next: (res) => {
        console.log(res);
        this.heroHuman = res;
        console.log(this.heroHuman);
      },
    }
    )

    this.heroService.getTopHeroByRace("Dwarf").subscribe({
      next: (res) => {
        console.log(res);
        this.heroDwarf = res;
        console.log(this.heroHuman);
      },
    }
    )

    this.heroService.getTopHeroByRace("Elf").subscribe({
      next: (res) => {
        console.log(res);
        this.heroElf = res;
        console.log(this.heroHuman);
      },
    }
    )
  }
}