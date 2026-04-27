export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface StatisticsDTO {
  totalItems: number;
  pendingItems: number;
  approvedItems: number;
  rejectedItems: number;
  claimedItems: number;
  returnedItems: number;
  returnRate: number;
  lineStatistics: LineStatistics[];
  itemTypeStatistics: ItemTypeStatistics[];
  monthStatistics: MonthStatistics[];
}

export interface LineStatistics {
  lineId: number;
  lineName: string;
  count: number;
  percentage: number;
}

export interface ItemTypeStatistics {
  typeId: number;
  typeName: string;
  count: number;
  percentage: number;
}

export interface MonthStatistics {
  month: string;
  total: number;
  returned: number;
}
