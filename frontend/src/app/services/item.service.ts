import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LostFoundItem, LostFoundItemRequest, ItemType, ItemStatus, SubwayLine, ItemTypeCategory } from '../models/item.model';
import { ApiResponse, PageResponse } from '../models/common.model';

@Injectable({
  providedIn: 'root'
})
export class ItemService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getApprovedItems(itemType?: ItemType, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<LostFoundItem>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (itemType) {
      params = params.set('itemType', itemType);
    }
    
    return this.http.get<ApiResponse<PageResponse<LostFoundItem>>>(`${this.API_URL}/public/items`, { params });
  }

  getItemById(id: number): Observable<ApiResponse<LostFoundItem>> {
    return this.http.get<ApiResponse<LostFoundItem>>(`${this.API_URL}/public/items/${id}`);
  }

  getMyItems(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<LostFoundItem>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<ApiResponse<PageResponse<LostFoundItem>>>(`${this.API_URL}/items/my`, { params });
  }

  createItem(request: LostFoundItemRequest): Observable<ApiResponse<LostFoundItem>> {
    return this.http.post<ApiResponse<LostFoundItem>>(`${this.API_URL}/items`, request);
  }

  deleteItem(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/items/${id}`);
  }

  getSubwayLines(): Observable<ApiResponse<SubwayLine[]>> {
    return this.http.get<ApiResponse<SubwayLine[]>>(`${this.API_URL}/public/subway-lines`);
  }

  getItemTypes(): Observable<ApiResponse<ItemTypeCategory[]>> {
    return this.http.get<ApiResponse<ItemTypeCategory[]>>(`${this.API_URL}/public/item-types`);
  }

  getAllItems(status?: ItemStatus, itemType?: ItemType, subwayLineId?: number, itemTypeId?: number, 
               keyword?: string, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<LostFoundItem>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (status) {
      params = params.set('status', status);
    }
    if (itemType) {
      params = params.set('itemType', itemType);
    }
    if (subwayLineId) {
      params = params.set('subwayLineId', subwayLineId.toString());
    }
    if (itemTypeId) {
      params = params.set('itemTypeId', itemTypeId.toString());
    }
    if (keyword) {
      params = params.set('keyword', keyword);
    }
    
    return this.http.get<ApiResponse<PageResponse<LostFoundItem>>>(`${this.API_URL}/admin/items`, { params });
  }

  approveItem(id: number): Observable<ApiResponse<LostFoundItem>> {
    return this.http.put<ApiResponse<LostFoundItem>>(`${this.API_URL}/admin/items/${id}/approve`, {});
  }

  rejectItem(id: number, reason?: string): Observable<ApiResponse<LostFoundItem>> {
    return this.http.put<ApiResponse<LostFoundItem>>(`${this.API_URL}/admin/items/${id}/reject`, reason);
  }

  markAsReturned(id: number): Observable<ApiResponse<LostFoundItem>> {
    return this.http.put<ApiResponse<LostFoundItem>>(`${this.API_URL}/admin/items/${id}/returned`, {});
  }

  adminDeleteItem(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/admin/items/${id}`);
  }
}
