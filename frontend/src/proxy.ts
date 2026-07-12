import { NextRequest, NextResponse } from 'next/server';

/**
 * Redirect unauthenticated users to /login.
 *
 * The JWT is delivered as an HttpOnly `auth_token` cookie by the backend
 * (`AuthController.loginExternalUser`). This middleware just checks for
 * the cookie's presence — signature verification happens at the API
 * layer via `JwtAuthenticationFilter`, not here.
 */
export function proxy(request: NextRequest) {
  const token = request.cookies.get('auth_token')?.value;

  if (!token) {
    const loginUrl = new URL('/login', request.url);
    if (request.nextUrl.pathname !== '/') {
      loginUrl.searchParams.set('returnTo', request.nextUrl.pathname);
    }
    return NextResponse.redirect(loginUrl);
  }

  // Authenticated user hitting root → redirect to home page
  if (request.nextUrl.pathname === '/') {
    return NextResponse.redirect(new URL('/users', request.url));
  }

  return NextResponse.next();
}

/**
 * Apply to all routes EXCEPT:
 *  - /login (auth page itself)
 *  - /forgot-password
 *  - /api/* (backend proxy — Spring handles its own auth)
 *  - /_next/* (static assets, HMR)
 *  - /images/*, /fonts/*, /favicon.ico, etc. (public assets)
 *
 * fonts/ exclusion is load-bearing: without it every @font-face request from
 * an unauthenticated context (e.g. the login page itself) 307-redirects to
 * /login, the font fails to decode, and the entire page falls back to Arial
 * (root cause of parity finding LOGIN-P2's "fallback face").
 */
export const config = {
  matcher: [
    '/((?!login|forgot-password|api/|_next/|images/|fonts/|favicon\\.ico|next\\.svg|vercel\\.svg|qa_).*)',
  ],
};
