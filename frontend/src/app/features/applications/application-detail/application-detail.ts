import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApplicationStatus, Application, StatusHistoryEntry } from '../../../core/models/application.model';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';
import { STATUS_OPTIONS } from '../../../shared/constants/application-labels';
import { JobTypeLabelPipe } from '../../../shared/pipes/job-type-label.pipe';
import { ApplicationService } from '../services/application.service';

@Component({
  selector: 'app-application-detail',
  imports: [ReactiveFormsModule, RouterLink, StatusBadge, JobTypeLabelPipe],
  templateUrl: './application-detail.html',
  styleUrl: './application-detail.scss',
})
export class ApplicationDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly applicationService = inject(ApplicationService);
  private readonly fb = inject(FormBuilder);

  private readonly applicationId = Number(this.route.snapshot.paramMap.get('id'));

  protected readonly statusOptions = STATUS_OPTIONS;
  protected readonly application = signal<Application | null>(null);
  protected readonly history = signal<StatusHistoryEntry[]>([]);
  protected readonly loading = signal(true);
  protected readonly statusChangeError = signal<string | null>(null);
  protected readonly isChangingStatus = signal(false);

  protected readonly statusForm = this.fb.nonNullable.group({
    status: ['', Validators.required],
    comment: [''],
  });

  constructor() {
    this.loadApplication();
    this.loadHistory();
  }

  protected onChangeStatus(): void {
    if (this.statusForm.invalid) {
      this.statusForm.markAllAsTouched();
      return;
    }

    this.isChangingStatus.set(true);
    this.statusChangeError.set(null);
    const raw = this.statusForm.getRawValue();

    this.applicationService
      .changeStatus(this.applicationId, {
        status: raw.status as ApplicationStatus,
        comment: raw.comment || undefined,
      })
      .subscribe({
        next: (application) => {
          this.application.set(application);
          this.statusForm.reset({ status: '', comment: '' });
          this.isChangingStatus.set(false);
          this.loadHistory();
        },
        error: () => {
          this.statusChangeError.set('Impossible de changer le statut, réessaie.');
          this.isChangingStatus.set(false);
        },
      });
  }

  protected delete(): void {
    const application = this.application();
    if (!application) {
      return;
    }
    const confirmed = confirm(`Supprimer la candidature "${application.company} — ${application.position}" ?`);
    if (!confirmed) {
      return;
    }
    this.applicationService.delete(application.id).subscribe(() => this.router.navigateByUrl('/applications'));
  }

  private loadApplication(): void {
    this.applicationService.get(this.applicationId).subscribe({
      next: (application) => {
        this.application.set(application);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  private loadHistory(): void {
    this.applicationService.getHistory(this.applicationId).subscribe({
      next: (history) => this.history.set(history),
      error: () => this.history.set([]),
    });
  }
}
