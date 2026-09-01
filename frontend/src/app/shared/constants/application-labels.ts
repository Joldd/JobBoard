import { ApplicationStatus, JobType } from '../../core/models/application.model';

// Le backend garde ses valeurs d'enum en anglais (contrat API stable, indépendant de la
// langue d'affichage) ; ces libellés sont la seule couche de traduction, centralisée ici
// plutôt qu'éparpillée dans chaque template.
export const STATUS_LABELS: Record<ApplicationStatus, string> = {
  TO_APPLY: 'À postuler',
  APPLIED: 'Postulé',
  FOLLOW_UP: 'Relance',
  HR_INTERVIEW: 'Entretien RH',
  TECHNICAL_INTERVIEW: 'Entretien technique',
  OFFER: 'Offre',
  REJECTED: 'Refusé',
  WITHDRAWN: 'Abandonné',
};

export const JOB_TYPE_LABELS: Record<JobType, string> = {
  CDI: 'CDI',
  CDD: 'CDD',
  INTERNSHIP: 'Stage',
  ALTERNANCE: 'Alternance',
  FREELANCE: 'Freelance',
};

export const STATUS_OPTIONS = Object.entries(STATUS_LABELS).map(([value, label]) => ({
  value: value as ApplicationStatus,
  label,
}));

export const JOB_TYPE_OPTIONS = Object.entries(JOB_TYPE_LABELS).map(([value, label]) => ({
  value: value as JobType,
  label,
}));
