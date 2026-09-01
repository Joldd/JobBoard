import { Injectable } from '@angular/core';
import { AuthenticatedUser } from '../models/auth.model';

const TOKEN_KEY = 'jobboard_token';
const USER_KEY = 'jobboard_user';

/**
 * Isole l'accès à localStorage derrière un service dédié : le reste de l'app ne sait
 * pas où/comment le token est persisté, ce qui rend le choix de stockage réversible.
 *
 * Compromis assumé : localStorage plutôt qu'un cookie httpOnly. Un cookie httpOnly
 * protégerait mieux contre le vol de token par XSS, mais demanderait au backend de
 * gérer des cookies + une vraie stratégie CSRF, pour un gain limité sur une appli à
 * usage personnel. Piste d'amélioration si le projet évolue vers un vrai multi-utilisateurs.
 */
@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  save(token: string, user: AuthenticatedUser): void {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getUser(): AuthenticatedUser | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthenticatedUser;
    } catch {
      return null;
    }
  }

  clear(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }
}
