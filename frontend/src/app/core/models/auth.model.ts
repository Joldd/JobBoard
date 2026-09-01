// Miroir des DTOs backend (com.jobboard.dto.auth.*).

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  name: string;
}

/** Ce qu'on garde en mémoire/localStorage : jamais le token ici, il a son propre stockage. */
export interface AuthenticatedUser {
  email: string;
  name: string;
}
