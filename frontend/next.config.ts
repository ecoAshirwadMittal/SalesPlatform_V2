import type { NextConfig } from "next";
import path from "path";

// Security headers applied to every response (security review 2026-07-10, M-1).
// The CSP is intentionally permissive on scripts/styles ('unsafe-inline' /
// 'unsafe-eval') so it does not break the Next.js runtime; frame-ancestors,
// object-src, base-uri and form-action are still locked down. Tighten to a
// nonce-based script-src in a follow-up once the app is validated against it.
const securityHeaders = [
  { key: "X-Frame-Options", value: "DENY" },
  { key: "X-Content-Type-Options", value: "nosniff" },
  { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
  { key: "Permissions-Policy", value: "camera=(), microphone=(), geolocation=()" },
  { key: "Strict-Transport-Security", value: "max-age=31536000; includeSubDomains" },
  {
    key: "Content-Security-Policy",
    value: [
      "default-src 'self'",
      "script-src 'self' 'unsafe-inline' 'unsafe-eval'",
      "style-src 'self' 'unsafe-inline'",
      "img-src 'self' data: blob:",
      "font-src 'self' data:",
      "connect-src 'self'",
      "frame-ancestors 'none'",
      "object-src 'none'",
      "base-uri 'self'",
      "form-action 'self'",
    ].join("; "),
  },
];

const nextConfig: NextConfig = {
  turbopack: {
    root: path.resolve(__dirname),
  },
  // Disable the dev-mode build indicator (the "N" badge in the bottom-left
  // corner) so it doesn't appear in Playwright pixel-compare screenshots.
  devIndicators: false,
  // Dev-only: allow cross-origin HMR / RSC chunk requests from ngrok tunnels
  // so the frontend can be exposed via `ngrok http 3000` for external testing.
  allowedDevOrigins: ['*.ngrok-free.dev', '*.ngrok-free.app', '*.ngrok.app'],
  async headers() {
    return [{ source: "/:path*", headers: securityHeaders }];
  },
  async rewrites() {
    return [
      {
        source: '/api/v1/:path*',
        destination: 'http://localhost:8080/api/v1/:path*',
      },
    ];
  },
};

export default nextConfig;
