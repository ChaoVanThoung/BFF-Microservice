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
}

/**
 * Generic API call function for BFF pattern
 * All requests go through the API Gateway with session cookies
 */
export async function apiCall<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  try {
    // Ensure endpoint starts with /
    const path = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
    
    const response = await fetch(`${API_GATEWAY_URL}${path}`, {
      ...options,
      credentials: "include", // Always include session cookies
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
        ...options.headers,
      },
    });

    if (!response.ok) {
      // Handle authentication errors
      if (response.status === 401) {
        return {
          success: false,
          error: "401: Unauthorized - Please log in",
        };
      }

      // Handle other errors
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
 * Uses session-based authentication
 */
export async function getCurrentUser(): Promise<ApiResponse<UserInfo>> {
  return apiCall<UserInfo>("/api/auth/me");
}

/**
 * Logout the current user
 * Invalidates session and clears cookies
 */
export async function logout(): Promise<void> {
  try {
    const response = await fetch(`${API_GATEWAY_URL}/api/auth/logout`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    });

    // Always redirect to login, even if logout fails
    // This ensures user is logged out on frontend
    window.location.href = "/login";
    
  } catch (error) {
    console.error("Logout failed:", error);
    // Still redirect to login page
    window.location.href = "/login";
  }
}