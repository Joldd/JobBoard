import { ApplicationStatus, JobType } from '../../core/models/application.model';

/**
 * Palette catégorielle validée (voir skill dataviz / references/palette.md) : 8 teintes
 * dans un ORDRE FIXE qui passe les checks de séparation CVD et de perception normale sur
 * les paires adjacentes. Ne jamais réordonner ces hex — l'ordre EST le mécanisme de
 * sécurité (vérifié avec scripts/validate_palette.js, pas à l'œil). ApplicationStatus a
 * exactement 8 valeurs, déclarées dans cet ordre pipeline : mapping direct sur les 8
 * slots, qui est aussi l'ordre d'affichage des barres du graphique "par statut" — c'est
 * cette combinaison (mêmes hex, même ordre) qui a été validée, pas les hex seuls.
 */
const CATEGORICAL_PALETTE = [
  '#2a78d6', // 1 blue
  '#eb6834', // 2 orange
  '#1baf7a', // 3 aqua
  '#eda100', // 4 yellow
  '#e87ba4', // 5 magenta
  '#008300', // 6 green
  '#4a3aa7', // 7 violet
  '#e34948', // 8 red
] as const;

export const STATUS_COLORS: Record<ApplicationStatus, string> = {
  TO_APPLY: CATEGORICAL_PALETTE[0],
  APPLIED: CATEGORICAL_PALETTE[1],
  FOLLOW_UP: CATEGORICAL_PALETTE[2],
  HR_INTERVIEW: CATEGORICAL_PALETTE[3],
  TECHNICAL_INTERVIEW: CATEGORICAL_PALETTE[4],
  OFFER: CATEGORICAL_PALETTE[5],
  REJECTED: CATEGORICAL_PALETTE[6],
  WITHDRAWN: CATEGORICAL_PALETTE[7],
};

// Sous-ensemble des 5 premiers slots (même ordre, donc même garantie d'adjacence).
export const JOB_TYPE_COLORS: Record<JobType, string> = {
  CDI: CATEGORICAL_PALETTE[0],
  CDD: CATEGORICAL_PALETTE[1],
  INTERNSHIP: CATEGORICAL_PALETTE[2],
  ALTERNANCE: CATEGORICAL_PALETTE[3],
  FREELANCE: CATEGORICAL_PALETTE[4],
};

// Ordre canonique des clés (= ordre pipeline = ordre de la palette validée) : à réutiliser
// partout où on itère sur les statuts/types de poste pour un graphique ou une légende,
// plutôt que l'ordre d'arrivée des données serveur (qui dépend de ce qui existe en base).
export const STATUS_ORDER = Object.keys(STATUS_COLORS) as ApplicationStatus[];
export const JOB_TYPE_ORDER = Object.keys(JOB_TYPE_COLORS) as JobType[];

// Rampe séquentielle (une seule teinte, "bleu") pour les graphiques de magnitude pure
// (top entreprises, tendance dans le temps) où la couleur ne porte pas d'identité.
export const SEQUENTIAL_BLUE = '#2a78d6';

// Rampe ordinale (même teinte, du plus clair au plus foncé) pour les étapes du funnel :
// "plus foncé" = étape plus avancée dans le pipeline, pas "plus de volume".
export const FUNNEL_RAMP = ['#6da7ec', '#3987e5', '#256abf', '#184f95'] as const;

export const CHART_TEXT_MUTED = '#898781';
export const CHART_GRIDLINE = '#e1e0d9';
