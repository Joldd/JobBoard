import { Component, computed, inject, signal } from '@angular/core';
import { DashboardStats } from '../../core/models/stats.model';
import { ChartComponent } from '../../shared/components/chart/chart';
import { StatTile } from '../../shared/components/stat-tile/stat-tile';
import { StatusLabelPipe } from '../../shared/pipes/status-label.pipe';
import { JOB_TYPE_COLORS, JOB_TYPE_ORDER, STATUS_COLORS, STATUS_ORDER } from '../../shared/constants/chart-palette';
import { JOB_TYPE_LABELS, STATUS_LABELS } from '../../shared/constants/application-labels';
import {
  buildFunnelChartConfig,
  buildJobTypeChartConfig,
  buildStatusChartConfig,
  buildTopCompaniesChartConfig,
  buildTrendChartConfig,
} from './chart-configs';
import { StatsService } from './services/stats.service';

@Component({
  selector: 'app-dashboard',
  imports: [ChartComponent, StatTile, StatusLabelPipe],
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html',
})
export class Dashboard {
  private readonly statsService = inject(StatsService);

  protected readonly stats = signal<DashboardStats | null>(null);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  // Constructeurs de ChartConfiguration importés de chart-configs.ts : chaque graphique
  // n'est monté (@if côté template) qu'une fois stats() renseigné, donc pas besoin de
  // recalculer ces objets à chaque cycle de détection de changements.
  protected readonly statusChartConfig = computed(() => {
    const stats = this.stats();
    return stats ? buildStatusChartConfig(stats) : null;
  });
  protected readonly jobTypeChartConfig = computed(() => {
    const stats = this.stats();
    return stats ? buildJobTypeChartConfig(stats) : null;
  });
  protected readonly topCompaniesChartConfig = computed(() => {
    const stats = this.stats();
    return stats ? buildTopCompaniesChartConfig(stats) : null;
  });
  protected readonly trendChartConfig = computed(() => {
    const stats = this.stats();
    return stats ? buildTrendChartConfig(stats) : null;
  });
  protected readonly funnelChartConfig = computed(() => {
    const stats = this.stats();
    return stats ? buildFunnelChartConfig(stats) : null;
  });

  // Légendes textuelles à côté des graphiques : satisfait la règle d'accessibilité
  // "l'identité ne repose jamais sur la seule couleur" (voir skill dataviz) sans
  // dépendre d'un plugin Chart.js supplémentaire pour des labels de données visibles.
  protected readonly statusLegend = computed(() => {
    const countByStatus = new Map((this.stats()?.byStatus ?? []).map((s) => [s.status, s.count]));
    return STATUS_ORDER.map((status) => ({
      label: STATUS_LABELS[status],
      color: STATUS_COLORS[status],
      count: countByStatus.get(status) ?? 0,
    }));
  });

  protected readonly jobTypeLegend = computed(() => {
    const countByJobType = new Map((this.stats()?.byJobType ?? []).map((j) => [j.jobType, j.count]));
    return JOB_TYPE_ORDER.filter((jobType) => (countByJobType.get(jobType) ?? 0) > 0).map((jobType) => ({
      label: JOB_TYPE_LABELS[jobType],
      color: JOB_TYPE_COLORS[jobType],
      count: countByJobType.get(jobType) ?? 0,
    }));
  });

  protected readonly totalApplications = computed(() =>
    (this.stats()?.byStatus ?? []).reduce((sum, s) => sum + s.count, 0),
  );

  protected readonly averageResponseLabel = computed(() => {
    const days = this.stats()?.averageResponseDays;
    return days == null ? '—' : `${days.toFixed(1)} j`;
  });

  protected readonly globalConversionLabel = computed(() => {
    const funnel = this.stats()?.conversionFunnel ?? [];
    if (funnel.length === 0 || funnel[0].fromCount === 0) {
      return '—';
    }
    const applied = funnel[0].fromCount;
    const offer = funnel[funnel.length - 1].toCount;
    return `${((offer / applied) * 100).toFixed(0)} %`;
  });

  constructor() {
    this.statsService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Impossible de charger les statistiques.');
        this.loading.set(false);
      },
    });
  }
}
