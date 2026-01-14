// Simple localStorage-based auth utility
export interface User {
  id: string
  username: string
}

export const login = (username: string, password: string): User | null => {
  // Simple validation - in production use real authentication
  if (username.length > 0 && password.length > 5) {
    const user: User = {
      id: `user_${Date.now()}`,
      username,
    }
    localStorage.setItem("user", JSON.stringify(user))
    return user
  }
  return null
}

export const logout = () => {
  localStorage.removeItem("user")
}

export const getCurrentUser = (): User | null => {
  if (typeof window === "undefined") return null
  const user = localStorage.getItem("user")
  return user ? JSON.parse(user) : null
}
