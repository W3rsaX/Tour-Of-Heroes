import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from "@angular/router";
import { Observable, of } from "rxjs";
import { Hero } from "./hero.model";
import { HeroService } from "./hero.service";

export const HeroResolver: ResolveFn<any> =
(route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
    heroService: HeroService = inject(HeroService)) :Observable<Hero> =>{


        const heroId = route.paramMap.get("heroId")

        if(heroId) {
            return heroService.getHero(Number(heroId));
        } else{

            const hero: Hero = {
                id: 0,
                name: '',
                gender: '',
                power: 0,
                race: ''
              }

            return of(hero);
        }



        
    }


