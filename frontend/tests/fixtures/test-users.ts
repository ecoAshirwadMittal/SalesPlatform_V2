/**
 * Test user credentials mapped from Flyway seed V15__seed_dev_roles_and_users.sql.
 *
 * PWS_User* roles are Bidder accounts with different buyer codes.
 * ADMIN* roles are SalesOps / Administrator accounts used for offer review.
 */

export interface TestUser {
  email: string;
  password: string;
  role: string;
  buyerCode?: string;
}

export const users: Record<string, TestUser> = {
  // ── Admin / SalesOps accounts ──
  ADMIN: { email: 'admin@test.com', password: 'Admin123!', role: 'Administrator' },
  SALESOPS: { email: 'salesops@test.com', password: 'SalesOps123!', role: 'SalesOps' },
  SALESREP: { email: 'salesrep@test.com', password: 'SalesRep123!', role: 'SalesRep' },
  CO_ADMIN: { email: 'coadmin@test.com', password: 'CoAdmin123!', role: 'Co-Admin' },

  // ── Buyer (Bidder) accounts ──
  BIDDER: { email: 'bidder@buyerco.com', password: 'Bidder123!', role: 'Bidder' },
  DIRECT_ADMIN: { email: 'directadmin@test.com', password: 'Direct123!', role: 'ecoAtmDirectAdmin' },
};

export type UserKey = keyof typeof users;
