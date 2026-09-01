import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Posé sur la route parente MainLayout (voir app.routes.ts) : protège toutes les
 * routes enfants en un seul endroit plutôt que guard par guard sur chaque feature.
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated() || router.createUrlTree(['/login']);
};
