import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ApplicationDetail } from './application-detail';

describe('ApplicationDetail', () => {
  let component: ApplicationDetail;
  let fixture: ComponentFixture<ApplicationDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApplicationDetail],
      // provideHttpClientTesting : le composant déclenche de vrais appels HTTP dès son
      // constructeur (chargement de la candidature + de l'historique) ; sans mock du
      // backend HTTP, le test tenterait une vraie requête réseau vers un serveur absent.
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(ApplicationDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
