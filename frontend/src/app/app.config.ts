import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    // withFetch : HttpClient s'appuie sur l'API fetch native plutôt que XHR
    // (recommandation Angular actuelle, meilleure interop avec les intercepteurs
    // fonctionnels qu'on ajoutera à l'étape auth).
    provideHttpClient(withFetch()),
  ]
};
