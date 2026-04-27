import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, StatisticsDTO } from '../models/common.model';
import { ItemStatus } from '../models/item.model';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private readonly API_URL = 'http://localhost:8080/api/admin/statistics';

  constructor(private http: HttpClient) {}

  getStatistics(): Observable<ApiResponse<StatisticsDTO>> {
    return this.http.get<ApiResponse<StatisticsDTO>>(`${this.API_URL}`);
  }

  exportItems(status?: ItemStatus): Observable<Blob> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    
    return this.http.get(`${this.API_URL}/export/items`, { 
      params,
      responseType: 'blob' 
    });
  }

  exportStatistics(): Observable<Blob> {
    return this.http.get(`${this.API_URL}/export/statistics`, { 
      responseType: 'blob' 
    });
  }
}
