import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from '../navbar/navbar';

/**
 * Coquille visuelle de l'application : barre de navigation fixe + zone de contenu
 * où le routeur projette la page active. Les routes protégées sont montées comme
 * enfants de cette route (voir app.routes.ts), donc ce composant n'a pas à se
 * soucier lui-même de l'authentification.
 */
@Component({
  imports: [RouterOutlet, Navbar],
  selector: 'app-main-layout',
  styleUrl: './main-layout.scss',
  templateUrl: './main-layout.html',
})
export class MainLayout {}
