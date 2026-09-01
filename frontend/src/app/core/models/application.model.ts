// Miroir des DTOs/enums backend (com.jobboard.dto.application.*, entity.enums.*).

export type JobType = 'CDI' | 'CDD' | 'INTERNSHIP' | 'ALTERNANCE' | 'FREELANCE';

export type ApplicationStatus =
  | 'TO_APPLY'
  | 'APPLIED'
  | 'FOLLOW_UP'
  | 'HR_INTERVIEW'
  | 'TECHNICAL_INTERVIEW'
  | 'OFFER'
  | 'REJECTED'
  | 'WITHDRAWN';

export interface Application {
  id: number;
  company: string;
  position: string;
  jobType: JobType;
  jobOfferUrl: string | null;
  applicationDate: string; // ISO "yyyy-MM-dd"
  currentStatus: ApplicationStatus;
  estimatedSalary: number | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateApplicationRequest {
  company: string;
  position: string;
  jobType: JobType;
  jobOfferUrl?: string;
  applicationDate: string;
  initialStatus?: ApplicationStatus;
  estimatedSalary?: number;
  notes?: string;
}

export interface UpdateApplicationRequest {
  company: string;
  position: string;
  jobType: JobType;
  jobOfferUrl?: string;
  applicationDate: string;
  estimatedSalary?: number;
  notes?: string;
}

export interface StatusChangeRequest {
  status: ApplicationStatus;
  comment?: string;
  changedAt?: string;
}

export interface StatusHistoryEntry {
  id: number;
  status: ApplicationStatus;
  changedAt: string;
  comment: string | null;
}

export interface ApplicationFilters {
  status?: ApplicationStatus;
  jobType?: JobType;
  company?: string;
  dateFrom?: string;
  dateTo?: string;
  page?: number;
  size?: number;
}

/** Miroir de org.springframework.data.domain.Page tel que sérialisé par Jackson. */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // page courante, 0-based
  size: number;
}
