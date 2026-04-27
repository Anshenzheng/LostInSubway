export interface Claim {
  id: number;
  itemId: number;
  itemTitle: string;
  claimerId: number;
  claimerName: string;
  claimReason: string;
  proofImages: string;
  status: ClaimStatus;
  rejectReason: string;
  adminRemark: string;
  createdAt: string;
  updatedAt: string;
}

export enum ClaimStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export interface ClaimRequest {
  itemId: number;
  claimReason: string;
  proofImages?: string;
}
