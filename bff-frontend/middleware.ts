// middleware.ts (place in root of Next.js project)
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_GATEWAY_URL || 'http://localhost:8080';

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Allow public routes (no authentication required)
  const publicRoutes = ['/', '/login'];
  if (
    publicRoutes.includes(pathname) ||
    pathname.startsWith('/_next') ||
    pathname === '/favicon.ico' ||
    pathname.startsWith('/images') ||
    pathname.startsWith('/fonts')
  ) {
    return NextResponse.next();
  }

  // For protected routes, check authentication via Gateway
  try {
    const response = await fetch(`${API_GATEWAY_URL}/api/auth/status`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Cookie': request.headers.get('cookie') || '',
      },
    });

    if (response.ok) {
      const data = await response.json();
      
      if (data.authenticated) {
        return NextResponse.next();
      }
    }
  } catch (error) {
    console.error('Auth check failed:', error);
  }

  // Not authenticated, redirect to login
  const loginUrl = new URL('/login', request.url);
  return NextResponse.redirect(loginUrl);
}

export const config = {
  matcher: [
    /*
     * Match all request paths except:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
};