import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Un 401 en dehors des routes /auth/** signifie un token absent/expiré/invalide sur une
 * route protégée : on déconnecte proprement plutôt que de laisser l'utilisateur face à
 * des appels API qui échouent en boucle silencieusement.
 *
 * Les 401 venant de /auth/login (mauvais mot de passe) sont volontairement exclus :
 * ce n'est pas une session qui expire, c'est une tentative de connexion qui échoue —
 * le composant Login gère déjà ce cas lui-même.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/')) {
        authService.logout();
      }
      return throwError(() => error);
    }),
  );
};
