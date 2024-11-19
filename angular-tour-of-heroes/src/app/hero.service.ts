import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Hero } from './hero.model';
import { Observable } from 'rxjs';


@Injectable({ providedIn: 'root' })
export class HeroService {

  api= "http://localhost:8080";

  constructor(private httpClient: HttpClient) {
   }
   public saveHero(hero: Hero): Observable<Hero> { 
    return this.httpClient.post<Hero>(`${this.api}/hero/save`, hero)
   }

   public getHeroes(sort: string, sortType: string, filterValue: string, PageSize: number, PageIndex: number): Observable<Hero[]> {
    return this.httpClient.get<Hero[]>(`${this.api}/hero/get`, {
      params: {"Sort": sort, "SortType": sortType,"FilterValue": filterValue,"PageSize": PageSize, "PageIndex": PageIndex}
    });
   }

   public getTopHeroByRace(race: string): Observable<Hero[]> {
    return this.httpClient.get<Hero[]>(`${this.api}/hero/getTop`, {
      params: {"Race": race}
    });
   }

   public deleteHero(heroId: number) {
    return this.httpClient.delete(`${this.api}/hero/delete/${heroId}`)
   }

   public getHero(heroId: number) {
    return this.httpClient.get<Hero>(`${this.api}/hero/get/${heroId}`);
   }

   public getLength(like: string) {
    return this.httpClient.get<number>(`${this.api}/hero/get/length`,{
      params: {"Like": like}
    });
   }

   public updateHero(hero: Hero): Observable<Hero> {
    return this.httpClient.put<Hero>(`${this.api}/hero/update`, hero)
   }

   public saveHeroesFromFile(formData: FormData): Observable<FormData> {
    return this.httpClient.post<FormData>(`${this.api}/hero/save/file`, formData)
   }

   public getHeroesFromFileCsv(like: string){
    return this.httpClient.get(`${this.api}/hero/get/file/csv`,{responseType: 'blob',params: {"Like": like}})
   }
   public getHeroesFromFileJson(like: string){
    return this.httpClient.get(`${this.api}/hero/get/file/json`,{responseType: 'blob',params: {"Like": like}});
   }
   public getHeroesFromFileXml(like: string){
    return this.httpClient.get(`${this.api}/hero/get/file/xml`,{responseType: 'blob',params: {"Like": like}})
   }
   
}