"use client";

/**
 * Deep-link surface for an individual PO (e.g. shared URL, audit-log
 * link, future "browse all" entry-point). The new landing
 * (purchase-orders/page.tsx) is the primary way users get to a PO —
 * via the week dropdowns at the top — so this page is now a thin
 * wrapper that just supplies the back affordance + page title.
 *
 * The body (header card + Upload/Download + 8-column grid) lives in
 * the shared <PurchaseOrderEditor /> component so this surface and
 * the landing surface stay byte-identical below the title.
 */

import { useParams } from "next/navigation";
import Link from "next/link";
import { PurchaseOrderEditor } from "../PurchaseOrderEditor";

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const TEXT_MUTED = "#606671";

export default function EditPurchaseOrderPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);

  return (
    <div>
      <Link
        href="/admin/auctions-data-center/purchase-orders"
        style={{ color: TEAL, fontSize: 14, textDecoration: "none" }}
      >
        ← Back to Purchase Order
      </Link>

      <header style={{ margin: "0.5rem 0 1.5rem" }}>
        <h2 style={{
          margin: 0,
          fontSize: "42px",
          fontWeight: 500,
          lineHeight: "54.6px",
          color: TEXT,
        }}>
          PO #{id}
        </h2>
        <p style={{ margin: "0.25rem 0 0", color: TEXT_MUTED, fontSize: 14 }}>
          Direct link — use the landing page&#39;s week dropdowns to browse other POs.
        </p>
      </header>

      <PurchaseOrderEditor poId={id} />
    </div>
  );
}
