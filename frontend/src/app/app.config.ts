import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    // withFetch : HttpClient s'appuie sur l'API fetch native plutôt que XHR
    // (recommandation Angular actuelle, meilleure interop avec les intercepteurs
    // fonctionnels ci-dessous).
    // Ordre des intercepteurs : authInterceptor attache le token AVANT que la requête
    // parte ; errorInterceptor observe la réponse et déconnecte sur un 401 non lié au
    // login lui-même.
    provideHttpClient(withFetch(), withInterceptors([authInterceptor, errorInterceptor])),
  ],
};
