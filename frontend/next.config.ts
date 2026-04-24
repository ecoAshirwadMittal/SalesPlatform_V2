import type { NextConfig } from "next";
import path from "path";

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
