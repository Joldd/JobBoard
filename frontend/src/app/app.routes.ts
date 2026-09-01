import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout/main-layout';

/**
 * Toutes les routes "applicatives" sont des enfants de MainLayout (navbar + contenu).
 * Chaque feature est chargée à la demande via loadComponent/loadChildren : le bundle
 * initial ne contient que le layout, pas le code de chaque page.
 *
 * Une route canActivate (guard d'authentification) sera ajoutée sur ce nœud parent
 * à l'étape suivante, pour protéger toutes les routes enfants en un seul endroit.
 */
export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
      },
    ],
  },
];
