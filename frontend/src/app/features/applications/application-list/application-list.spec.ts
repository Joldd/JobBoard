import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ApplicationList } from './application-list';

describe('ApplicationList', () => {
  let component: ApplicationList;
  let fixture: ComponentFixture<ApplicationList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApplicationList],
      // provideHttpClientTesting : le composant charge la liste dès son constructeur.
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(ApplicationList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
