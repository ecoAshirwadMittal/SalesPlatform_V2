'use client';

import { useState } from 'react';
import {
  deletePhoto,
  photoBlobUrl,
  type PhotoMetadata,
} from '@/lib/partialCreditClient';
import styles from '../detail.module.css';

interface PhotoGalleryProps {
  photos: PhotoMetadata[];
  /** Authenticated user's id — controls which trash icons render. */
  viewerUserId: number;
  /**
   * When true, hides every delete control regardless of ownership.
   * Caller sets this once the parent request is APPROVED / DECLINED.
   */
  readOnly: boolean;
  onDeleted: (photoId: number) => void;
}

/**
 * Thumbnail grid. Each tile lazy-loads the blob from the server-side
 * stream URL; the auth cookie flows automatically because the URL is
 * same-origin. Clicking a tile opens an inline lightbox; the trash icon
 * appears only on the viewer's own uploads (admin override happens at
 * the server, so the gallery treats admin-deletes as a separate flow we
 * don't need today).
 */
export function PhotoGallery({
  photos,
  viewerUserId,
  readOnly,
  onDeleted,
}: PhotoGalleryProps) {
  const [openId, setOpenId] = useState<number | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  if (photos.length === 0) {
    return <p className={styles.galleryEmpty}>No photos uploaded yet.</p>;
  }

  async function handleDelete(photoId: number) {
    setError(null);
    setDeletingId(photoId);
    try {
      await deletePhoto(photoId);
      onDeleted(photoId);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Delete failed');
    } finally {
      setDeletingId(null);
    }
  }

  const opened = photos.find((p) => p.id === openId) ?? null;

  return (
    <div className={styles.gallery}>
      {error && (
        <div className={styles.dropzoneError} role="alert">
          {error}
        </div>
      )}
      <ul className={styles.galleryGrid}>
        {photos.map((photo) => {
          const ownedByViewer = photo.uploadedByUserId === viewerUserId;
          const canDelete = !readOnly && ownedByViewer;
          return (
            <li key={photo.id} className={styles.galleryTile}>
              <button
                type="button"
                className={styles.galleryTileButton}
                onClick={() => setOpenId(photo.id)}
                aria-label={`Open ${photo.originalFilename}`}
              >
                <img
                  src={photoBlobUrl(photo.id)}
                  alt={photo.originalFilename}
                  loading="lazy"
                  className={styles.galleryThumb}
                />
              </button>
              <div className={styles.galleryCaption}>
                <span className={styles.galleryFilename}>{photo.originalFilename}</span>
                {canDelete && (
                  <button
                    type="button"
                    className={styles.galleryDelete}
                    onClick={() => handleDelete(photo.id)}
                    disabled={deletingId === photo.id}
                    aria-label={`Delete ${photo.originalFilename}`}
                  >
                    {deletingId === photo.id ? 'Deleting…' : 'Delete'}
                  </button>
                )}
              </div>
            </li>
          );
        })}
      </ul>
      {opened && (
        <div
          className={styles.lightbox}
          role="dialog"
          aria-label={opened.originalFilename}
          onClick={() => setOpenId(null)}
        >
          <img
            src={photoBlobUrl(opened.id)}
            alt={opened.originalFilename}
            className={styles.lightboxImage}
          />
        </div>
      )}
    </div>
  );
}
