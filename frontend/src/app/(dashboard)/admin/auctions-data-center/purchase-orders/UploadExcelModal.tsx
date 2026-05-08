"use client";

/**
 * Upload-line-items modal for an existing PO.
 *
 * Replaces the /[id]/upload dev-scaffold page (bare h1, unstyled
 * buttons, plain HTML file input, raw warning text). The modal lives
 * inside the editor surface so the user never leaves the grid they're
 * about to overwrite — and they see the line-item count drop to 0 +
 * repopulate live, which is reassuring after a destructive action.
 *
 * Two-state flow inside the modal:
 *   1. Picker  — pick file → submit
 *   2. Result  — show summary metrics + errors (if any), close button
 *
 * On clean success (zero errors), the parent reloads the grid and the
 * modal closes after a short delay. With errors, the modal stays open
 * so the admin can read them; closing reloads the grid all the same.
 */

import { useEffect, useRef, useState } from "react";
import { uploadPoDetails } from "@/lib/api/purchaseOrderClient";
import type { PODetailUploadResult } from "@/lib/types/purchaseOrder";

const TEAL = "#407874";
const TEXT = "#3C3C3C";
const TEXT_MUTED = "#606671";
const BORDER = "#D0D0D0";
const DIVIDER = "#E5E5E5";
const BG = "#F7F7F7";
const DANGER = "#a31b1b";
const WARN_BG = "#fff7e6";
const WARN_BORDER = "#f5d59a";
const WARN_TEXT = "#7a4d00";

interface Props {
  open: boolean;
  poId: number;
  poLabel: string;
  /**
   * Total line items currently on the PO. Drives whether we surface
   * the wipe-and-replace warning prominently (>0) or quietly (=0).
   */
  existingLineItemCount: number;
  onClose: () => void;
  /**
   * Fired after an upload result is received (whether or not there
   * were errors). Parent should reload the PO's details so the user
   * sees the new state.
   */
  onUploaded: (result: PODetailUploadResult) => void;
}

export default function UploadExcelModal({
  open, poId, poLabel, existingLineItemCount, onClose, onUploaded,
}: Props) {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState<PODetailUploadResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [dragOver, setDragOver] = useState(false);
  const fileRef = useRef<HTMLInputElement>(null);

  // Reset all state when the modal closes so the next open is fresh.
  useEffect(() => {
    if (open) return;
    setFile(null);
    setUploading(false);
    setResult(null);
    setError(null);
    setDragOver(false);
    if (fileRef.current) fileRef.current.value = "";
  }, [open]);

  // Auto-close on clean-success (matches a "save and dismiss" pattern
  // — if there's nothing to read, don't make the user click).
  useEffect(() => {
    if (!result || result.errors.length > 0) return;
    const t = setTimeout(() => onClose(), 1800);
    return () => clearTimeout(t);
  }, [result, onClose]);

  useEffect(() => {
    if (!open) return;
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape" && !uploading) onClose();
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, uploading, onClose]);

  if (!open) return null;

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!file) return;
    setUploading(true);
    setError(null);
    try {
      const r = await uploadPoDetails(poId, file);
      setResult(r);
      onUploaded(r);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Upload failed");
    } finally {
      setUploading(false);
    }
  }

  function onDrop(e: React.DragEvent) {
    e.preventDefault();
    setDragOver(false);
    const f = e.dataTransfer.files?.[0];
    if (f) setFile(f);
  }

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="upload-po-title"
      style={{
        position: "fixed",
        inset: 0,
        background: "rgba(17, 45, 50, 0.45)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 1000,
        padding: "1rem",
      }}
      onClick={(e) => {
        if (e.target === e.currentTarget && !uploading) onClose();
      }}
    >
      <div
        style={{
          background: "#fff",
          borderRadius: 6,
          width: "100%",
          maxWidth: 640,
          maxHeight: "90vh",
          overflow: "auto",
          boxShadow: "0 20px 60px rgba(0,0,0,0.25)",
        }}
      >
        <header style={{
          padding: "1rem 1.25rem",
          borderBottom: `1px solid ${BORDER}`,
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}>
          <div>
            <h3 id="upload-po-title" style={{ margin: 0, fontSize: 20, fontWeight: 500, color: TEXT }}>
              Upload Line Items
            </h3>
            <p style={{ margin: "0.15rem 0 0", color: TEXT_MUTED, fontSize: 13 }}>
              PO #{poId} · {poLabel}
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            disabled={uploading}
            aria-label="Close"
            style={{
              background: "none",
              border: 0,
              fontSize: 24,
              lineHeight: 1,
              color: TEXT_MUTED,
              cursor: uploading ? "default" : "pointer",
              padding: 0,
            }}
          >
            ×
          </button>
        </header>

        <div style={{ padding: "1.25rem" }}>
          {/* Result branch: we have an upload outcome to show. */}
          {result ? (
            <UploadResult result={result} onClose={onClose} />
          ) : (
            <form onSubmit={onSubmit}>
              {/* Wipe-and-replace warning — louder when there are
                  rows about to be deleted. */}
              {existingLineItemCount > 0 ? (
                <div role="alert" style={{
                  background: WARN_BG,
                  border: `1px solid ${WARN_BORDER}`,
                  borderRadius: 4,
                  padding: "0.75rem 1rem",
                  marginBottom: "1rem",
                  fontSize: 14,
                  color: WARN_TEXT,
                  display: "flex",
                  gap: "0.6rem",
                  alignItems: "flex-start",
                }}>
                  <span aria-hidden="true" style={{ fontSize: 18, lineHeight: 1 }}>⚠</span>
                  <div>
                    <strong style={{ display: "block", marginBottom: 2 }}>
                      Wipe-and-replace
                    </strong>
                    All <strong>{existingLineItemCount}</strong> existing line item{existingLineItemCount === 1 ? "" : "s"} on this PO will be deleted before the new rows are inserted. This cannot be undone.
                  </div>
                </div>
              ) : (
                <p style={{ margin: "0 0 1rem", color: TEXT_MUTED, fontSize: 14 }}>
                  Pick the Excel file with this PO&#39;s line items. The file
                  fully defines the line-items state — uploading replaces
                  any rows on the PO.
                </p>
              )}

              {/* Drop-zone style file picker. */}
              <label
                onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
                onDragLeave={() => setDragOver(false)}
                onDrop={onDrop}
                style={{
                  display: "block",
                  border: `2px dashed ${dragOver ? TEAL : BORDER}`,
                  background: dragOver ? "#eef5f4" : BG,
                  borderRadius: 6,
                  padding: "1.5rem",
                  textAlign: "center",
                  cursor: uploading ? "default" : "pointer",
                  marginBottom: "1rem",
                  transition: "border-color 120ms, background 120ms",
                }}
              >
                <input
                  ref={fileRef}
                  type="file"
                  accept=".xlsx,.xls"
                  onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                  disabled={uploading}
                  style={{
                    position: "absolute",
                    width: 1, height: 1,
                    opacity: 0,
                    pointerEvents: "none",
                  }}
                />
                {file ? (
                  <div>
                    <div style={{
                      fontSize: 14, color: TEXT, fontWeight: 500,
                      display: "inline-flex", alignItems: "center", gap: "0.5rem",
                    }}>
                      <span aria-hidden="true">📄</span>
                      {file.name}
                    </div>
                    <div style={{ marginTop: 4, fontSize: 12, color: TEXT_MUTED }}>
                      {formatBytes(file.size)} · click or drop to replace
                    </div>
                  </div>
                ) : (
                  <div>
                    <div style={{ fontSize: 14, color: TEXT, fontWeight: 500 }}>
                      Drop an .xlsx file here or click to browse
                    </div>
                    <div style={{ marginTop: 4, fontSize: 12, color: TEXT_MUTED }}>
                      Excel format only · one sheet
                    </div>
                  </div>
                )}
              </label>

              {error && (
                <div role="alert" style={{
                  color: DANGER,
                  background: "#fde7e7",
                  border: "1px solid #f5c2c2",
                  padding: "0.5rem 0.75rem",
                  borderRadius: 4,
                  marginBottom: "1rem",
                  fontSize: 14,
                }}>
                  {error}
                </div>
              )}

              <footer style={{
                display: "flex",
                gap: "0.5rem",
                justifyContent: "flex-end",
                marginTop: "1rem",
                paddingTop: "1rem",
                borderTop: `1px solid ${BORDER}`,
              }}>
                <button
                  type="button"
                  onClick={onClose}
                  disabled={uploading}
                  style={ghostBtn(uploading)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={!file || uploading}
                  style={primaryBtn(!file || uploading)}
                >
                  {uploading ? "Uploading…" : "Upload"}
                </button>
              </footer>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

function UploadResult({
  result, onClose,
}: {
  result: PODetailUploadResult;
  onClose: () => void;
}) {
  const ok = result.errors.length === 0;
  return (
    <div>
      <div role="status" style={{
        background: ok ? "#e6f4ec" : WARN_BG,
        border: `1px solid ${ok ? "#9bc8af" : WARN_BORDER}`,
        borderRadius: 4,
        padding: "0.75rem 1rem",
        marginBottom: "1rem",
        fontSize: 14,
        color: ok ? "#155e34" : WARN_TEXT,
        display: "flex",
        gap: "0.6rem",
        alignItems: "flex-start",
      }}>
        <span aria-hidden="true" style={{ fontSize: 18, lineHeight: 1 }}>{ok ? "✓" : "⚠"}</span>
        <div>
          <strong style={{ display: "block", marginBottom: 2 }}>
            {ok ? "Upload complete" : `Upload completed with ${result.errors.length} error${result.errors.length === 1 ? "" : "s"}`}
          </strong>
          {ok
            ? "Closing automatically…"
            : "The errors below were skipped. The successfully-imported rows are already saved."}
        </div>
      </div>

      {/* Metric cards. */}
      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(3, 1fr)",
        gap: "0.5rem",
        marginBottom: result.errors.length > 0 ? "1rem" : 0,
      }}>
        <Metric label="Deleted" value={result.deletedCount} tone="muted" />
        <Metric label="Created" value={result.createdCount} tone="positive" />
        <Metric label="Skipped" value={result.skippedCount} tone={result.skippedCount > 0 ? "warn" : "muted"} />
      </div>

      {result.errors.length > 0 && (
        <div style={{
          background: "#fff",
          border: `1px solid ${BORDER}`,
          borderRadius: 4,
          overflow: "hidden",
          marginBottom: "1rem",
        }}>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 13 }}>
            <thead>
              <tr style={{ background: BG, borderBottom: `1px solid ${BORDER}` }}>
                <Th>Row</Th>
                <Th>Product</Th>
                <Th>Grade</Th>
                <Th>Buyer</Th>
                <Th>Reason</Th>
              </tr>
            </thead>
            <tbody>
              {result.errors.map((e, i) => (
                <tr key={i} style={{ borderBottom: `1px solid ${DIVIDER}` }}>
                  <Td>{e.rowNumber}</Td>
                  <Td>{e.productId || "—"}</Td>
                  <Td>{e.grade || "—"}</Td>
                  <Td>{e.buyerCode || "—"}</Td>
                  <Td>{e.reason}</Td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <footer style={{
        display: "flex",
        gap: "0.5rem",
        justifyContent: "flex-end",
        marginTop: "1rem",
        paddingTop: "1rem",
        borderTop: `1px solid ${BORDER}`,
      }}>
        <button
          type="button"
          onClick={onClose}
          style={primaryBtn(false)}
        >
          Done
        </button>
      </footer>
    </div>
  );
}

function Metric({
  label, value, tone,
}: {
  label: string;
  value: number;
  tone: "positive" | "warn" | "muted";
}) {
  const colorByTone = {
    positive: "#176c4d",
    warn: "#a07f00",
    muted: TEXT_MUTED,
  } as const;
  return (
    <div style={{
      background: BG,
      border: `1px solid ${BORDER}`,
      borderRadius: 4,
      padding: "0.6rem 0.75rem",
    }}>
      <div style={{ fontSize: 12, color: TEXT_MUTED, textTransform: "uppercase", letterSpacing: "0.04em" }}>
        {label}
      </div>
      <div style={{
        fontSize: 22, fontWeight: 500,
        color: colorByTone[tone],
        lineHeight: 1.2,
        marginTop: 2,
      }}>
        {value}
      </div>
    </div>
  );
}

function Th({ children }: { children: React.ReactNode }) {
  return (
    <th style={{
      textAlign: "left",
      padding: "8px 10px",
      color: TEXT,
      fontWeight: 500,
      fontSize: 12,
      letterSpacing: "0.02em",
      whiteSpace: "nowrap",
    }}>
      {children}
    </th>
  );
}

function Td({ children }: { children: React.ReactNode }) {
  return (
    <td style={{
      padding: "6px 10px",
      color: TEXT,
      whiteSpace: "nowrap",
    }}>
      {children}
    </td>
  );
}

function formatBytes(b: number): string {
  if (b < 1024) return b + " B";
  if (b < 1024 * 1024) return (b / 1024).toFixed(1) + " KB";
  return (b / 1024 / 1024).toFixed(2) + " MB";
}

function primaryBtn(disabled: boolean): React.CSSProperties {
  return {
    padding: "8px 22px",
    background: TEAL,
    color: "white",
    border: 0,
    borderRadius: 4,
    fontSize: 14,
    cursor: disabled ? "default" : "pointer",
    opacity: disabled ? 0.6 : 1,
    fontFamily: "inherit",
  };
}

function ghostBtn(disabled: boolean): React.CSSProperties {
  return {
    padding: "8px 18px",
    background: BG,
    color: TEXT,
    border: `1px solid ${BORDER}`,
    borderRadius: 4,
    fontSize: 14,
    cursor: disabled ? "default" : "pointer",
    fontFamily: "inherit",
  };
}
