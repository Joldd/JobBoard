import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApplicationStatus, JobType } from '../../../core/models/application.model';
import { JOB_TYPE_OPTIONS, STATUS_OPTIONS } from '../../../shared/constants/application-labels';
import { ApplicationService } from '../services/application.service';

/**
 * Un seul composant pour créer ET modifier une candidature : les deux formulaires sont
 * identiques à un champ près (le statut initial, qui n'a de sens qu'à la création — un
 * changement de statut sur une candidature existante passe par application-detail, pas
 * par ce formulaire). Éviter la duplication l'emporte ici sur la séparation stricte des
 * responsabilités, vu la taille du formulaire.
 */
@Component({
  selector: 'app-application-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './application-form.html',
  styleUrl: './application-form.scss',
})
export class ApplicationForm {
  private readonly fb = inject(FormBuilder);
  private readonly applicationService = inject(ApplicationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly jobTypeOptions = JOB_TYPE_OPTIONS;
  protected readonly statusOptions = STATUS_OPTIONS;

  protected readonly applicationId = signal<number | null>(null);
  protected readonly isEditMode = computed(() => this.applicationId() !== null);

  protected readonly errorMessage = signal<string | null>(null);
  protected readonly isSubmitting = signal(false);
  // false uniquement le temps de charger les valeurs existantes en mode édition : sans
  // ça, une saisie assez rapide pour devancer la réponse du GET se ferait écraser par
  // le patchValue() qui arrive après coup.
  protected readonly loading = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    company: ['', Validators.required],
    position: ['', Validators.required],
    jobType: ['', Validators.required],
    jobOfferUrl: [''],
    applicationDate: ['', Validators.required],
    initialStatus: [''],
    estimatedSalary: this.fb.control<number | null>(null),
    notes: [''],
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      return;
    }

    const id = Number(idParam);
    this.applicationId.set(id);
    this.loading.set(true);
    this.applicationService.get(id).subscribe({
      next: (application) => {
        this.form.patchValue({
          company: application.company,
          position: application.position,
          jobType: application.jobType,
          jobOfferUrl: application.jobOfferUrl ?? '',
          applicationDate: application.applicationDate,
          estimatedSalary: application.estimatedSalary,
          notes: application.notes ?? '',
        });
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Impossible de charger cette candidature.');
        this.loading.set(false);
      },
    });
  }

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    const raw = this.form.getRawValue();
    const id = this.applicationId();

    const request$ = id
      ? this.applicationService.update(id, {
          company: raw.company,
          position: raw.position,
          jobType: raw.jobType as JobType,
          jobOfferUrl: raw.jobOfferUrl || undefined,
          applicationDate: raw.applicationDate,
          estimatedSalary: raw.estimatedSalary ?? undefined,
          notes: raw.notes || undefined,
        })
      : this.applicationService.create({
          company: raw.company,
          position: raw.position,
          jobType: raw.jobType as JobType,
          jobOfferUrl: raw.jobOfferUrl || undefined,
          applicationDate: raw.applicationDate,
          initialStatus: (raw.initialStatus || undefined) as ApplicationStatus | undefined,
          estimatedSalary: raw.estimatedSalary ?? undefined,
          notes: raw.notes || undefined,
        });

    request$.subscribe({
      next: (application) => this.router.navigate(['/applications', application.id]),
      error: (err: HttpErrorResponse) => {
        this.errorMessage.set(
          err.status === 400
            ? 'Certains champs sont invalides, vérifie le formulaire.'
            : 'Une erreur est survenue, réessaie.',
        );
        this.isSubmitting.set(false);
      },
    });
  }
}
