"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { reserveBidClient } from "@/lib/reserveBidClient";
import type { ReserveBidAuditRow } from "@/lib/reserveBidTypes";

export default function AuditPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const [rows, setRows] = useState<ReserveBidAuditRow[]>([]);

  useEffect(() => {
    reserveBidClient.audit(id).then((r) => setRows(r.rows));
  }, [id]);

  return (
    <div style={{ padding: "1.5rem" }}>
      <h1>Audit Trail — Reserve Bid #{id}</h1>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr><th>Product</th><th>Grade</th><th>Old</th><th>New</th><th>Changed By</th><th>When</th></tr>
        </thead>
        <tbody>
          {rows.map((r) => (
            <tr key={r.id}>
              <td>{r.productId}</td><td>{r.grade}</td>
              <td>{r.oldPrice}</td><td>{r.newPrice}</td>
              <td>{r.changedByUsername ?? "—"}</td>
              <td>{new Date(r.createdDate).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
