import { Pipe, PipeTransform } from '@angular/core';
import { JobType } from '../../core/models/application.model';
import { JOB_TYPE_LABELS } from '../constants/application-labels';

@Pipe({ name: 'jobTypeLabel' })
export class JobTypeLabelPipe implements PipeTransform {
  transform(value: JobType): string {
    return JOB_TYPE_LABELS[value] ?? value;
  }
}
