import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { MainLayout } from './layout/main-layout/main-layout';

/**
 * /login et /register sont publiques, en dehors de MainLayout. Tout le reste passe
 * par MainLayout (navbar + contenu), protégé par un unique authGuard posé sur ce
 * nœud parent : chaque nouvelle feature ajoutée en enfant hérite de la protection
 * sans avoir à redéclarer un guard.
 *
 * Chaque feature est chargée à la demande via loadComponent : le bundle initial ne
 * contient que le layout et les pages d'auth, pas le code de chaque page protégée.
 */
export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register),
  },
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
      },
    ],
  },
];
