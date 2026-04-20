'use client';

import { useEffect, useState, type ReactNode } from 'react';
import { getAuthUser } from '@/lib/session';

interface RequireRoleProps {
  roles: string[];
  children: ReactNode;
}

type Status = 'checking' | 'allowed' | 'denied';

export function RequireRole({ roles, children }: RequireRoleProps): ReactNode {
  const [status, setStatus] = useState<Status>('checking');

  useEffect(() => {
    const user = getAuthUser();
    const userRoles = user?.roles ?? [];
    const allowed = userRoles.some((r) => roles.includes(r));
    setStatus(allowed ? 'allowed' : 'denied');
  }, [roles]);

  if (status === 'checking') return null;

  if (status === 'denied') {
    return (
      <div style={{ padding: 48, textAlign: 'center', fontFamily: "'Brandon Grotesque', 'Open Sans', Arial, sans-serif" }}>
        <h2 style={{ fontSize: 32, fontWeight: 300, color: '#3c3c3c', margin: '0 0 12px' }}>403 — Access Denied</h2>
        <p style={{ fontSize: 15, color: '#666', margin: 0 }}>
          You need one of these roles to view this page: <strong>{roles.join(', ')}</strong>.
        </p>
      </div>
    );
  }

  return <>{children}</>;
}
