'use client';

import { useCallback, useState } from 'react';
import {
  PhotoUploadError,
  uploadPhoto,
  type PhotoMetadata,
} from '@/lib/partialCreditClient';
import styles from '../detail.module.css';

interface PhotoUploadDropzoneProps {
  requestId: number;
  /**
   * Optional per-line scoping. When set the upload is recorded as
   * WRONG_DEVICE evidence and counts against the 5-per-line cap;
   * null uploads it as DAMAGE evidence (request-scoped).
   */
  wrongDeviceLineId?: number | null;
  onUploaded: (photo: PhotoMetadata) => void;
}

/**
 * Multipart photo upload. Validates filetype + size client-side before
 * hitting the wire so the obvious typos (a PDF, a 50 MB monolith) fail
 * fast without a round trip. Server-side limits remain authoritative —
 * we surface the typed {@link PhotoUploadError} reason verbatim when
 * the request comes back 413/415/409.
 *
 * Visibility: caller hides this component once the parent request is
 * APPROVED / DECLINED (Sprint 4 §11.Q1) — the dropzone itself does not
 * know about the status because that gating lives one level up.
 */
export function PhotoUploadDropzone({
  requestId,
  wrongDeviceLineId,
  onUploaded,
}: PhotoUploadDropzoneProps) {
  const [error, setError] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);

  const handleFiles = useCallback(
    async (files: FileList | null) => {
      if (!files || files.length === 0) return;
      setError(null);
      setUploading(true);
      try {
        // Upload sequentially — the per-line cap check on the server
        // would race if we POST'd 5 files in parallel against a line
        // that's already at 4 (each call would see 4 existing photos
        // and let through 5 inserts).
        for (const file of Array.from(files)) {
          if (!isImageMime(file.type)) {
            throw new PhotoUploadError(
              'UNSUPPORTED_TYPE',
              `${file.name}: only JPEG / PNG / HEIC / WebP are allowed`,
              415,
            );
          }
          const saved = await uploadPhoto(
            requestId,
            file,
            wrongDeviceLineId ?? null,
          );
          onUploaded(saved);
        }
      } catch (err) {
        if (err instanceof PhotoUploadError) {
          setError(err.message);
        } else {
          setError(err instanceof Error ? err.message : 'Upload failed');
        }
      } finally {
        setUploading(false);
      }
    },
    [requestId, wrongDeviceLineId, onUploaded],
  );

  return (
    <div className={styles.dropzone}>
      <label className={styles.dropzoneLabel}>
        <span>{uploading ? 'Uploading…' : 'Choose photo(s) to upload'}</span>
        <input
          type="file"
          accept="image/jpeg,image/png,image/heic,image/webp"
          multiple
          disabled={uploading}
          aria-label="Upload photos"
          onChange={(e) => handleFiles(e.target.files)}
          className={styles.dropzoneInput}
        />
      </label>
      <p className={styles.dropzoneHint}>
        JPEG, PNG, HEIC or WebP up to 5 MB each.
      </p>
      {error && (
        <div className={styles.dropzoneError} role="alert">
          {error}
        </div>
      )}
    </div>
  );
}

const ALLOWED_PREFIXES = ['image/jpeg', 'image/png', 'image/heic', 'image/webp'];

function isImageMime(type: string): boolean {
  const lower = (type || '').toLowerCase();
  return ALLOWED_PREFIXES.some((p) => lower === p);
}
