"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Loader2, CheckCircle, XCircle } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

export default function AuthCallbackPage() {
  const router = useRouter()
  const [status, setStatus] = useState<'processing' | 'success' | 'error'>('processing')
  const [message, setMessage] = useState('Completing authentication...')

  useEffect(() => {
    handleCallback()
  }, [])

  const handleCallback = async () => {
    try {
      // The OAuth2 flow is already completed by Spring Security at this point
      // The Gateway has set session cookies automatically
      // We just need to verify authentication and redirect
      
      setStatus('processing')
      setMessage('Verifying session...')

      // Small delay to ensure session cookie is set
      await new Promise(resolve => setTimeout(resolve, 500))

      // Verify we have a valid session by checking /me endpoint
      const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_GATEWAY_URL || "http://localhost:8080"
      const response = await fetch(`${API_GATEWAY_URL}/api/auth/me`, {
        credentials: 'include',
        headers: {
          'Accept': 'application/json'
        }
      })

      if (response.ok) {
        setStatus('success')
        setMessage('Authentication successful! Redirecting...')
        setTimeout(() => router.push('/products'), 1000)
      } else {
        setStatus('error')
        setMessage('Session verification failed')
        setTimeout(() => router.push('/login?error=true'), 2000)
      }
    } catch (error) {
      console.error('Callback verification error:', error)
      setStatus('error')
      setMessage('An error occurred during authentication')
      setTimeout(() => router.push('/login?error=true'), 2000)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-background to-muted">
      <Card className="w-full max-w-md mx-4">
        <CardHeader className="text-center">
          <div className="mx-auto mb-4">
            {status === 'processing' && (
              <Loader2 className="h-12 w-12 animate-spin text-primary" />
            )}
            {status === 'success' && (
              <CheckCircle className="h-12 w-12 text-green-500" />
            )}
            {status === 'error' && (
              <XCircle className="h-12 w-12 text-red-500" />
            )}
          </div>
          <CardTitle className="text-2xl">
            {status === 'processing' && 'Completing Sign In'}
            {status === 'success' && 'Success!'}
            {status === 'error' && 'Authentication Failed'}
          </CardTitle>
        </CardHeader>
        <CardContent className="text-center">
          <p className="text-muted-foreground">{message}</p>
          {status === 'processing' && (
            <div className="mt-4 text-xs text-muted-foreground">
              <p>🔒 Establishing secure session...</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}