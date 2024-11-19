import { HeroService } from '../hero.service';
import { Hero } from '../hero.model';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Component, ViewChild, AfterViewInit, ElementRef } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { PageEvent, MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { FormControl, FormGroup } from '@angular/forms';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-hero-list',
  templateUrl: './hero-list.component.html',
  styleUrl: './hero-list.component.css',
})


export class HeroListComponent implements AfterViewInit {
  herolist !: Hero[];
  displayedColumns: string[] = ['id', 'name', 'race', 'gender', 'power', 'edit', 'delete'];
  dataSource: MatTableDataSource<Hero>;
  typeControl = new FormControl('');
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('input') input: ElementRef<HTMLInputElement>;
  typeFile: any;

  pageSizeOptions = [10, 50, 100, 500];
  pageSize = 10;
  pageIndex = 0;
  length = 100;
  pageEvent: PageEvent;
  sortCol: string;
  sortType: string;
  filterValue: string;

  form = new FormGroup({
    typeControl: this.typeControl
  })

  constructor(private heroService: HeroService,
    private router: Router,
    private _liveAnnouncer: LiveAnnouncer,) {
    this.sortCol = "id";
    this.sortType = "asc";
    this.filterValue = "";
    this.getHeroList();
  }

  search(): void {
    this.getHeroList();
  }

  PageEvent(e: PageEvent): void {
    console.log("AFS");
    this.pageEvent = e;
    this.pageSize = e.pageSize;
    this.pageIndex = e.pageIndex;
    this.length = e.length;
    this.getHeroList();
  }

  getFile(): void {
    let blob: Blob;
    if (this.typeFile == undefined) {

    }
    else {
      switch (this.typeFile) {
        case "CSV":
          this.heroService.getHeroesFromFileCsv(this.filterValue).subscribe(
            {
              next: (res: Blob) => {
                saveAs(res, "Heroes.csv")
              },
              error: (err: HttpErrorResponse) => {
                console.log(err);
              }
            }
          );
          break;
        case "JSON":
          this.heroService.getHeroesFromFileJson(this.filterValue).subscribe(
            {
              next: (res: Blob) => {
                saveAs(res, "Heroes.json")
              },
              error: (err: HttpErrorResponse) => {
                console.log(err);
              }
            }
          );
          break;
        case "XML":
          this.heroService.getHeroesFromFileXml(this.filterValue).subscribe(
            {
              next: (res: Blob) => {
                saveAs(res, "Heroes.xml")
              },
              error: (err: HttpErrorResponse) => {
                console.log(err);
              }
            }
          );
          break;
      }
    }

  }

  ngAfterViewInit(): void {
    this.input.nativeElement.focus();
  }

  applyFilter(event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value;
  }

  deleteHero(heroId: number): void {
    console.log(heroId)
    this.heroService.deleteHero(heroId).subscribe(
      {
        next: (res) => {
          this.getHeroList();
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
        }
      }
    )
  }

  updateHero(heroId: number): void {
    this.router.navigate(['/heroes', { heroId: heroId }]);
  }

  getHeroList(): void {
    this.heroService.getLength(this.filterValue).subscribe(
      {
        next: (res: number) => {
          this.length = res;
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
        }
      }
    );
    this.heroService.getHeroes(this.sortCol, this.sortType, this.filterValue, this.pageSize, this.pageIndex).subscribe(
      {
        next: (res: Hero[]) => {
          this.herolist = res;
          this.dataSource = new MatTableDataSource<Hero>(this.herolist);
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
        }
      }
    );
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this.sortCol = this.sort.active;
      this.sortType = sortState.direction;
      this.getHeroList();
      this._liveAnnouncer.announce(`Sorted ${sortState.direction}ending`);
    } else {
      this.sortCol = "id";
      this.sortType = "asc";
      this.getHeroList();
      this._liveAnnouncer.announce('Sorting cleared');
    }
  }
}