import { Component, computed, input } from '@angular/core';
import { ApplicationStatus } from '../../../core/models/application.model';
import { STATUS_LABELS } from '../../constants/application-labels';

@Component({
  selector: 'app-status-badge',
  imports: [],
  templateUrl: './status-badge.html',
  styleUrl: './status-badge.scss',
})
export class StatusBadge {
  readonly status = input.required<ApplicationStatus>();

  protected readonly label = computed(() => STATUS_LABELS[this.status()]);
  // Utilisé comme classe CSS (voir status-badge.scss) : chaque statut a sa couleur.
  protected readonly modifierClass = computed(() => `status-badge--${this.status().toLowerCase()}`);
}
