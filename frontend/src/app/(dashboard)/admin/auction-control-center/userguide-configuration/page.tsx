'use client';

/**
 * Userguide Configuration — Phase 12
 *
 * Allows Administrators to upload, view, and manage the Buyer User Guide PDF
 * that appears in the bidder sidebar. The active guide is the file served at
 * GET /api/v1/bidder/docs/buyer-guide.
 *
 * Layout:
 *   1. Current file card — file name, upload date, uploader, byte size,
 *      Download + Replace actions
 *   2. Upload section — file input (.pdf only), size/type error surface
 *   3. History table — last 10 uploads (read-only, audit trail)
 */

import { useState, useEffect, useRef, useCallback } from 'react';
import Link from 'next/link';
import { apiFetch } from '@/lib/apiFetch';
import s from '../../../settings/pws-control-center/admin.module.css';

// ── Types ──────────────────────────────────────────────────────────────────────

interface GuideMetadata {
  id: number;
  fileName: string;
  contentType: string;
  byteSize: number;
  uploadedBy: number;
  uploaderName: string;
  uploadedAt: string;
  isActive: boolean;
}

interface GuideListResponse {
  active: GuideMetadata | null;
  history: GuideMetadata[];
}

// ── Constants ──────────────────────────────────────────────────────────────────

const API = '/api/v1/admin/buyer-user-guide';
const MAX_BYTES = 20 * 1024 * 1024; // 20 MB

// ── Helpers ────────────────────────────────────────────────────────────────────

function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
}

function formatDate(iso: string | null): string {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleDateString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  } catch {
    return iso;
  }
}

async function parseError(res: Response): Promise<string> {
  try {
    const body = await res.json() as Record<string, unknown>;
    return (body.message as string) || res.statusText;
  } catch {
    return res.statusText;
  }
}

// ── Page component ─────────────────────────────────────────────────────────────

export default function UserguideConfigurationPage() {
  const [data, setData] = useState<GuideListResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [banner, setBanner] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  // Upload state
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [fileError, setFileError] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiFetch(API);
      if (!res.ok) throw new Error(await parseError(res));
      const json = await res.json() as GuideListResponse;
      setData(json);
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { void load(); }, [load]);

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    setFileError(null);
    const file = e.target.files?.[0] ?? null;
    if (!file) { setSelectedFile(null); return; }

    // Client-side validation (backend validates again — defence in depth)
    if (file.type !== 'application/pdf') {
      setFileError('Only PDF files are accepted (.pdf).');
      setSelectedFile(null);
      return;
    }
    if (file.size > MAX_BYTES) {
      setFileError(`File too large (${formatBytes(file.size)}). Maximum is 20 MB.`);
      setSelectedFile(null);
      return;
    }
    setSelectedFile(file);
  }

  async function handleUpload() {
    if (!selectedFile) return;
    setUploading(true);
    setBanner(null);
    try {
      const form = new FormData();
      form.append('file', selectedFile);
      const res = await apiFetch(API, { method: 'POST', body: form });
      if (!res.ok) throw new Error(await parseError(res));
      setSelectedFile(null);
      if (fileInputRef.current) fileInputRef.current.value = '';
      setBanner({ type: 'success', message: 'Guide uploaded and activated successfully.' });
      await load();
    } catch (err) {
      setBanner({ type: 'error', message: String(err) });
    } finally {
      setUploading(false);
    }
  }

  function handleReplaceClick() {
    setBanner(null);
    setSelectedFile(null);
    setFileError(null);
    fileInputRef.current?.click();
  }

  return (
    <div className={s.pageContainer}>
      {/* Header */}
      <div className={s.pageHeader}>
        <h2 className={s.pageTitle}>Userguide Configuration</h2>
        <Link href="/admin/auction-control-center" className={s.backLink}>
          ← Back to Auction Control Center
        </Link>
      </div>

      {/* Banner */}
      {banner && (
        <div className={`${s.banner} ${banner.type === 'success' ? s.bannerSuccess : s.bannerError}`}>
          {banner.message}
        </div>
      )}

      {loading ? (
        <div className={s.loading}>Loading...</div>
      ) : (
        <>
          {/* ── Current File Card ─────────────────────────────────── */}
          <div className={s.card} style={{ marginBottom: 24 }}>
            <h3 className={s.sectionTitle}>Current Active Guide</h3>
            {data?.active ? (
              <table className={s.table}>
                <thead>
                  <tr>
                    <th>File Name</th>
                    <th>Size</th>
                    <th>Uploaded By</th>
                    <th>Uploaded At</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>{data.active.fileName}</td>
                    <td>{formatBytes(data.active.byteSize)}</td>
                    <td>{data.active.uploaderName}</td>
                    <td>{formatDate(data.active.uploadedAt)}</td>
                    <td className={s.actionsCell}>
                      {/* Download opens the live bidder endpoint in a new tab */}
                      <a
                        href="/api/v1/bidder/docs/buyer-guide"
                        target="_blank"
                        rel="noopener noreferrer"
                        className={s.actionBtn}
                        style={{ textDecoration: 'none', display: 'inline-block' }}
                      >
                        Download
                      </a>
                      <button className={s.actionBtn} onClick={handleReplaceClick}>
                        Replace
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            ) : (
              <p className={s.emptyState}>No guide has been uploaded yet.</p>
            )}
          </div>

          {/* ── Upload Section ────────────────────────────────────── */}
          <div className={s.card} style={{ marginBottom: 24 }}>
            <h3 className={s.sectionTitle}>
              {data?.active ? 'Replace Current Guide' : 'Upload Buyer User Guide'}
            </h3>
            <p style={{ fontSize: 13, color: '#555', marginBottom: 12 }}>
              Accepted format: PDF only. Maximum size: 20 MB.
              Uploading a new file immediately replaces the currently active guide.
            </p>

            {/* Hidden native file input */}
            <input
              ref={fileInputRef}
              type="file"
              accept="application/pdf,.pdf"
              style={{ display: 'none' }}
              aria-label="Choose PDF file to upload"
              onChange={handleFileChange}
            />

            <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
              <button
                type="button"
                className={s.actionBtn}
                onClick={() => fileInputRef.current?.click()}
              >
                Choose File
              </button>

              {selectedFile && (
                <span style={{ fontSize: 13, color: '#333' }}>
                  {selectedFile.name} ({formatBytes(selectedFile.size)})
                </span>
              )}

              {!selectedFile && !fileError && (
                <span style={{ fontSize: 13, color: '#999' }}>No file selected</span>
              )}
            </div>

            {fileError && (
              <p style={{ color: '#991b1b', fontSize: 13, marginTop: 8 }} role="alert">
                {fileError}
              </p>
            )}

            {selectedFile && (
              <div style={{ marginTop: 16 }}>
                <button
                  type="button"
                  className={s.saveBtn}
                  disabled={uploading}
                  onClick={handleUpload}
                >
                  {uploading ? 'Uploading...' : 'Upload & Activate'}
                </button>
                <button
                  type="button"
                  className={s.actionBtn}
                  style={{ marginLeft: 8 }}
                  disabled={uploading}
                  onClick={() => {
                    setSelectedFile(null);
                    setFileError(null);
                    if (fileInputRef.current) fileInputRef.current.value = '';
                  }}
                >
                  Cancel
                </button>
              </div>
            )}
          </div>

          {/* ── History Table ─────────────────────────────────────── */}
          <div className={s.card}>
            <h3 className={s.sectionTitle}>Upload History (last 10)</h3>
            {data?.history && data.history.length > 0 ? (
              <table className={s.table}>
                <thead>
                  <tr>
                    <th>File Name</th>
                    <th>Size</th>
                    <th>Uploaded By</th>
                    <th>Uploaded At</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {data.history.map((entry) => (
                    <tr key={entry.id}>
                      <td>{entry.fileName}</td>
                      <td>{formatBytes(entry.byteSize)}</td>
                      <td>{entry.uploaderName}</td>
                      <td>{formatDate(entry.uploadedAt)}</td>
                      <td>
                        {entry.isActive ? (
                          <span style={{
                            background: '#dcfce7', color: '#166534',
                            padding: '2px 8px', borderRadius: 12, fontSize: 12,
                          }}>
                            Active
                          </span>
                        ) : (
                          <span style={{ color: '#999', fontSize: 12 }}>Inactive</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p className={s.emptyState}>No upload history yet.</p>
            )}
          </div>
        </>
      )}
    </div>
  );
}
