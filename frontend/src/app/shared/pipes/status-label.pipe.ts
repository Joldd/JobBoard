import { Pipe, PipeTransform } from '@angular/core';
import { ApplicationStatus } from '../../core/models/application.model';
import { STATUS_LABELS } from '../constants/application-labels';

@Pipe({ name: 'statusLabel' })
export class StatusLabelPipe implements PipeTransform {
  transform(value: ApplicationStatus): string {
    return STATUS_LABELS[value] ?? value;
  }
}
