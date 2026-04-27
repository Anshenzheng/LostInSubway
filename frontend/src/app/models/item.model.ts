export interface LostFoundItem {
  id: number;
  itemType: ItemType;
  title: string;
  description: string;
  itemTypeId: number;
  itemTypeName: string;
  subwayLineId: number;
  subwayLineName: string;
  stationName: string;
  lostFoundTime: string;
  contactName: string;
  contactPhone: string;
  contactEmail: string;
  imageUrls: string;
  publisherId: number;
  publisherName: string;
  status: ItemStatus;
  rejectReason: string;
  adminRemark: string;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
}

export enum ItemType {
  LOST = 'LOST',
  FOUND = 'FOUND'
}

export enum ItemStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  CLAIMED = 'CLAIMED',
  RETURNED = 'RETURNED'
}

export interface LostFoundItemRequest {
  itemType: ItemType;
  title: string;
  description: string;
  itemTypeId?: number;
  subwayLineId?: number;
  stationName?: string;
  lostFoundTime?: string;
  contactName?: string;
  contactPhone?: string;
  contactEmail?: string;
  imageUrls?: string;
}

export interface SubwayLine {
  id: number;
  lineName: string;
  lineNumber: string;
  color: string;
  description: string;
  status: string;
}

export interface ItemTypeCategory {
  id: number;
  typeName: string;
  description: string;
  icon: string;
  sortOrder: number;
  status: string;
}
