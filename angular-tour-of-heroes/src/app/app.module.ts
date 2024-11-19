import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from './in-memory-data.service';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HeroDetailComponent } from './hero-detail/hero-detail.component';
import { HeroesComponent } from './heroes/heroes.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MatButtonModule } from '@angular/material/button';
import { HeroListComponent } from './hero-list/hero-list.component';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatRadioModule } from '@angular/material/radio';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { AsyncPipe } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatGridListModule } from '@angular/material/grid-list';


@NgModule({
    declarations: [
        AppComponent,
        DashboardComponent,
        HeroesComponent,
        HeroDetailComponent,
        HeroListComponent,
    ],
    bootstrap: [AppComponent],
    imports: [BrowserModule,
        FormsModule,
        AppRoutingModule,
        MatButtonModule,
        MatInputModule,
        MatIconModule,
        MatFormFieldModule,
        MatTableModule,
        MatSelectModule,
        MatSortModule,
        MatPaginator,
        AsyncPipe,
        MatGridListModule,
        MatPaginatorModule,
        MatRadioModule,
        FormsModule,
        MatAutocompleteModule,
        ReactiveFormsModule,
        HttpClientInMemoryWebApiModule.forRoot(InMemoryDataService, { dataEncapsulation: false })], providers: [provideHttpClient(withInterceptorsFromDi()), provideAnimationsAsync()]
})
export class AppModule { }