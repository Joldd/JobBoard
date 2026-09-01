import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  Application,
  ApplicationFilters,
  CreateApplicationRequest,
  Page,
  StatusChangeRequest,
  StatusHistoryEntry,
  UpdateApplicationRequest,
} from '../../../core/models/application.model';

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/applications`;

  list(filters: ApplicationFilters): Observable<Page<Application>> {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', filters.size ?? 20);

    if (filters.status) params = params.set('status', filters.status);
    if (filters.jobType) params = params.set('jobType', filters.jobType);
    if (filters.company) params = params.set('company', filters.company);
    if (filters.dateFrom) params = params.set('dateFrom', filters.dateFrom);
    if (filters.dateTo) params = params.set('dateTo', filters.dateTo);

    return this.http.get<Page<Application>>(this.baseUrl, { params });
  }

  get(id: number): Observable<Application> {
    return this.http.get<Application>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateApplicationRequest): Observable<Application> {
    return this.http.post<Application>(this.baseUrl, request);
  }

  update(id: number, request: UpdateApplicationRequest): Observable<Application> {
    return this.http.put<Application>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  changeStatus(id: number, request: StatusChangeRequest): Observable<Application> {
    return this.http.patch<Application>(`${this.baseUrl}/${id}/status`, request);
  }

  getHistory(id: number): Observable<StatusHistoryEntry[]> {
    return this.http.get<StatusHistoryEntry[]>(`${this.baseUrl}/${id}/history`);
  }
}
