// lib/api-client.ts

const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_GATEWAY_URL || "http://localhost:8080";

interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
}

interface UserInfo {
  username?: string;
  email?: string;
  name?: string;
  sub?: string;
  givenName?: string;
  familyName?: string;
}

interface Product {
  id: string;
  title: string;
  price: number;
}

/**
 * Generic API call function for BFF pattern
 */
export async function apiCall<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  try {
    const path = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
    
    const response = await fetch(`${API_GATEWAY_URL}${path}`, {
      ...options,
      credentials: "include", // Critical: Include session cookies
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
        ...options.headers,
      },
    });

    if (!response.ok) {
      if (response.status === 401) {
        return {
          success: false,
          error: "Authentication required",
        };
      }

      const errorText = await response.text();
      return {
        success: false,
        error: `${response.status}: ${errorText || response.statusText}`,
      };
    }

    const data = await response.json();
    return {
      success: true,
      data: data as T,
    };
  } catch (error) {
    console.error("API call failed:", error);
    return {
      success: false,
      error: error instanceof Error ? error.message : "Network error",
    };
  }
}

/**
 * Get current user information
 */
export async function getCurrentUser(): Promise<ApiResponse<UserInfo>> {
  return apiCall<UserInfo>("/api/auth/me");
}

/**
 * Check authentication status
 */
export async function checkAuthStatus(): Promise<boolean> {
  const result = await apiCall<{ authenticated: boolean }>("/api/auth/status");
  return result.success && result.data?.authenticated === true;
}

/**
 * Get all products
 */
export async function getProducts(): Promise<ApiResponse<Product[]>> {
  return apiCall<Product[]>("/api/products");
}

/**
 * Create a new product
 */
export async function createProduct(title: string, price: number): Promise<ApiResponse<Product>> {
  return apiCall<Product>("/api/products", {
    method: "POST",
    body: JSON.stringify({ title, price }),
  });
}

/**
 * Logout the current user
 */
export async function logout(): Promise<void> {
  try {
    await fetch(`${API_GATEWAY_URL}/logout`, {
      method: "POST",
      credentials: "include",
    });
  } catch (error) {
    console.error("Logout error:", error);
  } finally {
    // Always redirect to login page (through Gateway to clear session)
    window.location.href = "/login";
  }
}