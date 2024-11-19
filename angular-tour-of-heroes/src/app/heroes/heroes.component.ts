import { AfterViewInit, Component, ElementRef, OnInit, ViewChild, Renderer2, ContentChild } from '@angular/core';
import { Hero } from '../hero.model';
import { HeroService } from '../hero.service';
import { FormControl, FormGroup, NgForm, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-heroes.model',
  templateUrl: './heroes.component.html',
  styleUrls: ['./heroes.component.css'],

})
export class HeroesComponent implements OnInit, AfterViewInit {
  @ViewChild('fileInput') fileInput:ElementRef;
  @ViewChild('file') file: ElementRef;
  isCreateHero: boolean = true;
  nameControl = new FormControl('', [Validators.required, Validators.maxLength(24)]);
  powerControl = new FormControl('', [Validators.required, Validators.max(100), Validators.min(0)]);
  raceControl = new FormControl('', Validators.required);
  hero: any;
  protected gender;


  form = new FormGroup({
    nameControl: this.nameControl,
    powerControl: this.powerControl,
    raceControl: this.raceControl
  })

  constructor(private heroService: HeroService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private renderer: Renderer2,) {
  }
  ngAfterViewInit(): void {
    if (this.hero && this.hero.id > 0)
      this.renderer.setStyle(this.file.nativeElement, "visibility", "hidden");
  }

  ngOnInit(): void {
    this.hero = this.activatedRoute.snapshot.data['hero'];
    console.log(this.hero);
    if (this.hero && this.hero.id > 0) {
      this.isCreateHero = false;
      this.gender = this.hero.gender
    } 
    else {
      this.isCreateHero = true;
      this.hero.power = '';
    }
  }

  selectGender(gender: string): void {
    this.hero.gender = gender;
  }

  clearForm() {
    this.hero.id = 0;
    this.hero.name = '';
    this.hero.gender = '';
    this.gender = '';
    this.hero.power = '';
    this.hero.race = '';
  }

  uploadFile():void{
    this.fileInput.nativeElement.click();
  }

  fileUpload(event):void{
    const formData = new FormData();
    formData.append('file', event.target.files[0])
    console.log(formData);
    this.heroService.saveHeroesFromFile(formData).subscribe(
      {
        next: (res: FormData) => {
          console.log(res);
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
        }
      }
    )
    event.target.value = null;
  }

  saveHero(heroForm: NgForm): void {
    if (this.form.valid) {
      if (this.isCreateHero) {
        console.log(this.hero);
        this.heroService.saveHero(this.hero).subscribe(
          {
            next: (res: Hero) => {
              console.log(res);
              heroForm.reset();
              this.router.navigate(["/hero-list"]);
            },
            error: (err: HttpErrorResponse) => {
              console.log(err);
            }
          }
        );
      } else {
        this.heroService.updateHero(this.hero).subscribe(
          {
            next: (res: Hero) => {
              this.router.navigate(["/hero-list"]);
            },
            error: (err: HttpErrorResponse) => {
              console.log(err);
            }
          }
        );
      }
    }
  }
}