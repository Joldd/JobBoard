import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';
import { JOB_TYPE_OPTIONS, STATUS_OPTIONS } from '../../../shared/constants/application-labels';
import { JobTypeLabelPipe } from '../../../shared/pipes/job-type-label.pipe';
import { Application } from '../../../core/models/application.model';
import { ApplicationService } from '../services/application.service';

const PAGE_SIZE = 20;

@Component({
  selector: 'app-application-list',
  imports: [ReactiveFormsModule, RouterLink, StatusBadge, JobTypeLabelPipe],
  templateUrl: './application-list.html',
  styleUrl: './application-list.scss',
})
export class ApplicationList {
  private readonly applicationService = inject(ApplicationService);
  private readonly fb = inject(FormBuilder);

  protected readonly statusOptions = STATUS_OPTIONS;
  protected readonly jobTypeOptions = JOB_TYPE_OPTIONS;

  protected readonly filterForm = this.fb.nonNullable.group({
    company: [''],
    status: [''],
    jobType: [''],
    dateFrom: [''],
    dateTo: [''],
  });

  protected readonly applications = signal<Application[]>([]);
  protected readonly totalElements = signal(0);
  protected readonly page = signal(0);
  protected readonly loading = signal(false);

  constructor() {
    this.load();

    // Filtrage "live" avec un debounce : évite une requête à chaque frappe dans le
    // champ entreprise tout en gardant les selects/dates réactifs sans bouton "Filtrer".
    this.filterForm.valueChanges.pipe(debounceTime(300), takeUntilDestroyed()).subscribe(() => {
      this.page.set(0);
      this.load();
    });
  }

  protected load(): void {
    this.loading.set(true);
    const raw = this.filterForm.getRawValue();

    this.applicationService
      .list({
        company: raw.company || undefined,
        status: (raw.status || undefined) as Application['currentStatus'] | undefined,
        jobType: (raw.jobType || undefined) as Application['jobType'] | undefined,
        dateFrom: raw.dateFrom || undefined,
        dateTo: raw.dateTo || undefined,
        page: this.page(),
        size: PAGE_SIZE,
      })
      .subscribe({
        next: (result) => {
          this.applications.set(result.content);
          this.totalElements.set(result.totalElements);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  protected goToPage(page: number): void {
    this.page.set(page);
    this.load();
  }

  protected delete(application: Application): void {
    const confirmed = confirm(`Supprimer la candidature "${application.company} — ${application.position}" ?`);
    if (!confirmed) {
      return;
    }
    this.applicationService.delete(application.id).subscribe(() => this.load());
  }

  protected get totalPages(): number {
    return Math.max(Math.ceil(this.totalElements() / PAGE_SIZE), 1);
  }
}
