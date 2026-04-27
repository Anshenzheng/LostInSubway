import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Claim, ClaimRequest, ClaimStatus } from '../models/claim.model';
import { ApiResponse, PageResponse } from '../models/common.model';

@Injectable({
  providedIn: 'root'
})
export class ClaimService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  createClaim(request: ClaimRequest): Observable<ApiResponse<Claim>> {
    return this.http.post<ApiResponse<Claim>>(`${this.API_URL}/claims`, request);
  }

  getClaimById(id: number): Observable<ApiResponse<Claim>> {
    return this.http.get<ApiResponse<Claim>>(`${this.API_URL}/claims/${id}`);
  }

  getMyClaims(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Claim>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ApiResponse<PageResponse<Claim>>>(`${this.API_URL}/claims/my`, { params });
  }

  getAllClaims(status?: ClaimStatus, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Claim>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (status) {
      params = params.set('status', status);
    }
    
    return this.http.get<ApiResponse<PageResponse<Claim>>>(`${this.API_URL}/admin/claims`, { params });
  }

  approveClaim(id: number): Observable<ApiResponse<Claim>> {
    return this.http.put<ApiResponse<Claim>>(`${this.API_URL}/admin/claims/${id}/approve`, {});
  }

  rejectClaim(id: number, reason?: string): Observable<ApiResponse<Claim>> {
    return this.http.put<ApiResponse<Claim>>(`${this.API_URL}/admin/claims/${id}/reject`, reason);
  }
}
