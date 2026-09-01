import { ChartConfiguration } from 'chart.js';
import { DashboardStats } from '../../core/models/stats.model';
import {
  CHART_GRIDLINE,
  CHART_TEXT_MUTED,
  FUNNEL_RAMP,
  JOB_TYPE_COLORS,
  JOB_TYPE_ORDER,
  SEQUENTIAL_BLUE,
  STATUS_COLORS,
  STATUS_ORDER,
} from '../../shared/constants/chart-palette';
import { JOB_TYPE_LABELS, STATUS_LABELS } from '../../shared/constants/application-labels';

export function buildStatusChartConfig(stats: DashboardStats): ChartConfiguration {
  const countByStatus = new Map(stats.byStatus.map((s) => [s.status, s.count]));

  return {
    type: 'bar',
    data: {
      labels: STATUS_ORDER.map((status) => STATUS_LABELS[status]),
      datasets: [
        {
          data: STATUS_ORDER.map((status) => countByStatus.get(status) ?? 0),
          backgroundColor: STATUS_ORDER.map((status) => STATUS_COLORS[status]),
          borderRadius: 4,
          maxBarThickness: 24,
        },
      ],
    },
    options: {
      indexAxis: 'y',
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: CHART_GRIDLINE } },
        y: { grid: { display: false }, ticks: { color: CHART_TEXT_MUTED } },
      },
    },
  };
}

export function buildJobTypeChartConfig(stats: DashboardStats): ChartConfiguration {
  const countByJobType = new Map(stats.byJobType.map((j) => [j.jobType, j.count]));
  // Un type de poste jamais utilisé par cet utilisateur n'occupe pas de part du camembert.
  const present = JOB_TYPE_ORDER.filter((jobType) => (countByJobType.get(jobType) ?? 0) > 0);

  return {
    type: 'doughnut',
    data: {
      labels: present.map((jobType) => JOB_TYPE_LABELS[jobType]),
      datasets: [
        {
          data: present.map((jobType) => countByJobType.get(jobType) ?? 0),
          backgroundColor: present.map((jobType) => JOB_TYPE_COLORS[jobType]),
          borderWidth: 2,
          borderColor: '#fcfcfb',
        },
      ],
    },
    options: {
      maintainAspectRatio: false,
      plugins: { legend: { position: 'bottom', labels: { color: CHART_TEXT_MUTED, boxWidth: 12 } } },
    },
  };
}

export function buildTopCompaniesChartConfig(stats: DashboardStats): ChartConfiguration {
  return {
    type: 'bar',
    data: {
      labels: stats.topCompanies.map((c) => c.company),
      datasets: [
        {
          data: stats.topCompanies.map((c) => c.count),
          backgroundColor: SEQUENTIAL_BLUE,
          borderRadius: 4,
          maxBarThickness: 24,
        },
      ],
    },
    options: {
      indexAxis: 'y',
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: CHART_GRIDLINE } },
        y: { grid: { display: false }, ticks: { color: CHART_TEXT_MUTED } },
      },
    },
  };
}

export function buildTrendChartConfig(stats: DashboardStats): ChartConfiguration {
  return {
    type: 'line',
    data: {
      labels: stats.applicationsOverTime.map((m) => m.month),
      datasets: [
        {
          data: stats.applicationsOverTime.map((m) => m.count),
          borderColor: SEQUENTIAL_BLUE,
          backgroundColor: SEQUENTIAL_BLUE,
          tension: 0.25,
          pointRadius: 4,
          pointHoverRadius: 6,
        },
      ],
    },
    options: {
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks: { color: CHART_TEXT_MUTED } },
        y: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: CHART_GRIDLINE } },
      },
    },
  };
}

export function buildFunnelChartConfig(stats: DashboardStats): ChartConfiguration {
  if (stats.conversionFunnel.length === 0) {
    return { type: 'bar', data: { labels: [], datasets: [] } };
  }

  // fromCount de chaque étape = combien de candidatures ont atteint CETTE étape ; le
  // toCount de la dernière étape complète la série (nombre ayant atteint l'étape finale).
  const stages = [
    stats.conversionFunnel[0].fromStage,
    ...stats.conversionFunnel.map((step) => step.toStage),
  ];
  const counts = [
    stats.conversionFunnel[0].fromCount,
    ...stats.conversionFunnel.map((step) => step.toCount),
  ];

  return {
    type: 'bar',
    data: {
      labels: stages.map((stage) => STATUS_LABELS[stage]),
      datasets: [
        {
          data: counts,
          backgroundColor: FUNNEL_RAMP.slice(0, stages.length),
          borderRadius: 4,
          maxBarThickness: 40,
        },
      ],
    },
    options: {
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks: { color: CHART_TEXT_MUTED } },
        y: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: CHART_GRIDLINE } },
      },
    },
  };
}
