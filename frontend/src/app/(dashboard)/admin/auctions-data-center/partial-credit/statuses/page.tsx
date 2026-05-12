'use client';

import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  listStatuses,
  updateStatus,
  type StatusConfigRow,
  type StatusConfigPatch,
} from '@/lib/statusConfigClient';
import styles from './statusConfig.module.css';

/**
 * SPKB-3664 — status config admin grid.
 *
 * Renders one inline-editable row per seeded status from
 * {@code partial_credit.credit_request_statuses}. Edits are committed
 * via PATCH; the response replaces the row in local state so the table
 * always reflects the persisted snapshot.
 *
 * Color hex is the only field with a tight format constraint; we
 * validate client-side before PATCH so the obvious typos never round
 * trip. The buyer landing reads the same column from the API, so a
 * color change here propagates without redeploy.
 */

const COLOR_HEX_RE = /^#[0-9A-Fa-f]{6}$/;

interface RowDraft {
  internalStatusText: string;
  externalStatusText: string;
  colorHex: string;
  sortOrder: number;
  showInUserCounters: boolean;
}

function toDraft(row: StatusConfigRow): RowDraft {
  return {
    internalStatusText: row.internalStatusText,
    externalStatusText: row.externalStatusText,
    colorHex: row.colorHex,
    sortOrder: row.sortOrder,
    showInUserCounters: row.showInUserCounters,
  };
}

export default function StatusConfigPage() {
  const [rows, setRows] = useState<StatusConfigRow[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [draft, setDraft] = useState<RowDraft | null>(null);
  const [rowError, setRowError] = useState<string | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [saving, setSaving] = useState<boolean>(false);

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError(null);
    try {
      const next = await listStatuses();
      setRows(next);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load statuses';
      setLoadError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const startEdit = useCallback((row: StatusConfigRow) => {
    setEditingId(row.id);
    setDraft(toDraft(row));
    setRowError(null);
  }, []);

  const cancelEdit = useCallback(() => {
    setEditingId(null);
    setDraft(null);
    setRowError(null);
  }, []);

  const isColorValid = useMemo<boolean>(() => {
    if (!draft) return true;
    return COLOR_HEX_RE.test(draft.colorHex);
  }, [draft]);

  const save = useCallback(async () => {
    if (!draft || editingId == null) return;
    if (!COLOR_HEX_RE.test(draft.colorHex)) {
      setRowError('Color must be a six-digit hex like #FFAA00');
      return;
    }
    if (draft.internalStatusText.length > 100) {
      setRowError('Internal text exceeds 100 characters');
      return;
    }
    if (draft.externalStatusText.length > 100) {
      setRowError('External text exceeds 100 characters');
      return;
    }
    setSaving(true);
    setRowError(null);
    const patch: StatusConfigPatch = {
      internalStatusText: draft.internalStatusText,
      externalStatusText: draft.externalStatusText,
      colorHex: draft.colorHex.toUpperCase(),
      sortOrder: draft.sortOrder,
      showInUserCounters: draft.showInUserCounters,
    };
    try {
      const updated = await updateStatus(editingId, patch);
      // Optimistically replace in place; preserve grid order even if
      // sort_order changed — the server returns sort_order ascending on
      // the next reload, so the local order self-corrects on refresh.
      setRows((prev) => prev.map((r) => (r.id === updated.id ? updated : r)));
      setToast('Status updated');
      // Toast auto-dismisses; we set a short clear-timer so two
      // back-to-back saves don't lose the second update's confirmation.
      window.setTimeout(() => setToast(null), 3000);
      setEditingId(null);
      setDraft(null);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Update failed';
      setRowError(message);
    } finally {
      setSaving(false);
    }
  }, [draft, editingId]);

  return (
    <div className={styles.page}>
      <div className={styles.headerRow}>
        <h1 className={styles.heading}>Partial Credit — Status Configuration</h1>
      </div>
      <p className={styles.description}>
        Edit the display text and color for each system status. The system
        status itself is fixed by the application — only the cosmetic
        fields can change.
      </p>

      {toast && <div className={styles.toast}>{toast}</div>}
      {loadError && <div className={styles.errorAlert}>{loadError}</div>}

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <th>System Status</th>
              <th>Internal Text</th>
              <th>External Text</th>
              <th>Color</th>
              <th>Sort Order</th>
              <th>Show in Counters</th>
              <th>Default</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={8}>Loading…</td>
              </tr>
            )}
            {!loading && rows.length === 0 && !loadError && (
              <tr>
                <td colSpan={8}>No status rows found.</td>
              </tr>
            )}
            {rows.map((row) => {
              const isEditing = editingId === row.id && draft !== null;
              return (
                <tr key={row.id}>
                  <td className={styles.systemStatusCell}>{row.systemStatus}</td>
                  <td>
                    {isEditing ? (
                      <input
                        className={styles.textInput}
                        type="text"
                        value={draft.internalStatusText}
                        onChange={(e) =>
                          setDraft({ ...draft, internalStatusText: e.target.value })
                        }
                        maxLength={100}
                      />
                    ) : (
                      row.internalStatusText
                    )}
                  </td>
                  <td>
                    {isEditing ? (
                      <input
                        className={styles.textInput}
                        type="text"
                        value={draft.externalStatusText}
                        onChange={(e) =>
                          setDraft({ ...draft, externalStatusText: e.target.value })
                        }
                        maxLength={100}
                      />
                    ) : (
                      row.externalStatusText
                    )}
                  </td>
                  <td>
                    <div className={styles.colorCell}>
                      <span
                        className={styles.colorSwatch}
                        style={{
                          backgroundColor: isEditing
                            ? isColorValid
                              ? draft.colorHex
                              : row.colorHex
                            : row.colorHex,
                        }}
                        aria-label={`Color swatch for ${row.systemStatus}`}
                      />
                      {isEditing ? (
                        <input
                          className={`${styles.colorHexInput} ${
                            isColorValid ? '' : styles.colorHexInputInvalid
                          }`}
                          type="text"
                          value={draft.colorHex}
                          onChange={(e) =>
                            setDraft({ ...draft, colorHex: e.target.value })
                          }
                          maxLength={7}
                          placeholder="#RRGGBB"
                        />
                      ) : (
                        <span>{row.colorHex}</span>
                      )}
                    </div>
                    {isEditing && rowError && (
                      <div className={styles.rowError}>{rowError}</div>
                    )}
                  </td>
                  <td>
                    {isEditing ? (
                      <input
                        className={styles.numberInput}
                        type="number"
                        value={draft.sortOrder}
                        onChange={(e) =>
                          setDraft({
                            ...draft,
                            sortOrder: Number.parseInt(e.target.value, 10) || 0,
                          })
                        }
                      />
                    ) : (
                      row.sortOrder
                    )}
                  </td>
                  <td>
                    {isEditing ? (
                      <input
                        type="checkbox"
                        checked={draft.showInUserCounters}
                        onChange={(e) =>
                          setDraft({ ...draft, showInUserCounters: e.target.checked })
                        }
                      />
                    ) : row.showInUserCounters ? (
                      'Yes'
                    ) : (
                      'No'
                    )}
                  </td>
                  <td>{row.isDefault ? <span className={styles.defaultBadge}>Default</span> : '—'}</td>
                  <td className={styles.actionsCell}>
                    {isEditing ? (
                      <>
                        <button
                          type="button"
                          className={styles.buttonPrimary}
                          onClick={() => void save()}
                          disabled={saving}
                        >
                          {saving ? 'Saving…' : 'Save'}
                        </button>
                        <button
                          type="button"
                          className={styles.buttonGhost}
                          onClick={cancelEdit}
                          disabled={saving}
                        >
                          Cancel
                        </button>
                      </>
                    ) : (
                      <button
                        type="button"
                        className={styles.buttonGhost}
                        onClick={() => startEdit(row)}
                        disabled={editingId !== null}
                      >
                        Edit
                      </button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
