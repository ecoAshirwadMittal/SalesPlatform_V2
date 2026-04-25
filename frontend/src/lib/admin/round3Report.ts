import { apiFetch } from '../apiFetch';

/**
 * Single row in the R3 Bid Report by Buyer view.
 *
 * Shape mirrors {@code Round3ReportRow.java}. Decimal totals come back
 * as JSON numbers (Spring's default for {@code BigDecimal} → JSON), but
 * we type them as {@code number | null} since the column is nullable.
 */
export interface Round3ReportRow {
  id: number;
  auctionId: number | null;
  buyerCode: string | null;
  companyName: string | null;
  totalQuantity: number | null;
  totalPayout: number | null;
  /** ISO-8601 string from Spring's default Instant serialiser. */
  submittedDatetime: string | null;
}

export interface Round3ReportResponse {
  weekId: number;
  count: number;
  rows: Round3ReportRow[];
}

/**
 * Fetch every R3 report row whose parent auction's {@code week_id}
 * matches. Empty arrays are valid responses.
 */
export async function fetchRound3ReportByWeek(
  weekId: number,
): Promise<Round3ReportResponse> {
  const res = await apiFetch(`/api/v1/admin/round3-reports?weekId=${weekId}`);
  if (!res.ok) {
    throw new Error(`Failed to load R3 report: HTTP ${res.status}`);
  }
  return (await res.json()) as Round3ReportResponse;
}
