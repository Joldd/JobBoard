import { ApplicationStatus, JobType } from './application.model';

export interface StatusCount {
  status: ApplicationStatus;
  count: number;
}

export interface JobTypeCount {
  jobType: JobType;
  count: number;
}

export interface CompanyCount {
  company: string;
  count: number;
}

/** month au format ISO "yyyy-MM", trié chronologiquement par le backend. */
export interface MonthlyCount {
  month: string;
  count: number;
}

export interface ConversionStep {
  fromStage: ApplicationStatus;
  toStage: ApplicationStatus;
  fromCount: number;
  toCount: number;
  conversionRate: number;
}

export interface DashboardStats {
  byStatus: StatusCount[];
  byJobType: JobTypeCount[];
  topCompanies: CompanyCount[];
  conversionFunnel: ConversionStep[];
  averageResponseDays: number | null;
  applicationsOverTime: MonthlyCount[];
}
