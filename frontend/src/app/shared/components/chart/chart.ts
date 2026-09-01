import { AfterViewInit, Component, ElementRef, OnDestroy, input, viewChild } from '@angular/core';
import {
  ArcElement,
  BarController,
  BarElement,
  CategoryScale,
  Chart,
  ChartConfiguration,
  DoughnutController,
  Legend,
  LinearScale,
  LineController,
  LineElement,
  PointElement,
  Tooltip,
} from 'chart.js';

// Chart.js 4 est tree-shakable : seuls les éléments explicitement enregistrés sont
// inclus dans le bundle. On enregistre une fois, au chargement du module, uniquement
// ce dont le dashboard a besoin (bar, doughnut, line — pas de radar/polar/etc.).
Chart.register(
  BarController,
  DoughnutController,
  LineController,
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  LineElement,
  PointElement,
  Legend,
  Tooltip,
);

/**
 * Fine encapsulation de Chart.js (bibliothèque non-Angular) dans un composant standalone :
 * un seul endroit gère le cycle de vie (création après le rendu de la vue, destruction à
 * la sortie), le reste de l'app manipule uniquement des objets ChartConfiguration typés.
 *
 * Volontairement non réactif à un changement de `config()` après coup : chaque graphique
 * du dashboard n'est monté qu'une fois les statistiques déjà chargées (@if côté parent),
 * donc pas besoin de recréer le graphique en cours de vie du composant pour ce cas d'usage.
 */
@Component({
  selector: 'app-chart',
  imports: [],
  templateUrl: './chart.html',
  styleUrl: './chart.scss',
})
export class ChartComponent implements AfterViewInit, OnDestroy {
  readonly config = input.required<ChartConfiguration>();

  private readonly canvasRef = viewChild.required<ElementRef<HTMLCanvasElement>>('canvas');
  private chart?: Chart;

  ngAfterViewInit(): void {
    this.chart = new Chart(this.canvasRef().nativeElement, this.config());
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }
}
