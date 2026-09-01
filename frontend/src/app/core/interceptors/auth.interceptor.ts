import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Attache le Bearer token à chaque requête sortante, s'il y en a un. On ne restreint
 * pas aux seules routes /api : c'est sans danger d'envoyer le header vers /auth/login
 * ou /auth/register (le backend les ignore, ces routes sont publiques), et ça évite
 * de coupler l'intercepteur à la connaissance des routes publiques du backend.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();

  if (!token) {
    return next(req);
  }

  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
