export interface User {
  id: number;
  username: string;
  realName: string;
  phone: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
  updatedAt: string;
}

export enum UserRole {
  PASSENGER = 'PASSENGER',
  ADMIN = 'ADMIN'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  BANNED = 'BANNED'
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  realName?: string;
  phone?: string;
  email?: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  realName: string;
  role: string;
}
