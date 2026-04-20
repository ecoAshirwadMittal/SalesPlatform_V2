'use client';

import { useEffect, useRef, useState } from 'react';
import styles from './inventory.module.css';
import {
  createAuction,
  DuplicateAuctionTitleError,
  AuctionAlreadyExistsError,
} from '@/lib/auctions';

interface CreateAuctionModalProps {
  weekId: number;
  weekDisplay: string;
  onClose: () => void;
  onCreated: () => void;
}

const TITLE_PREFIX = 'Auction';

export function CreateAuctionModal({
  weekId,
  weekDisplay,
  onClose,
  onCreated,
}: CreateAuctionModalProps) {
  const [suffix, setSuffix] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [fieldError, setFieldError] = useState<string | null>(null);
  const [bannerError, setBannerError] = useState<string | null>(null);
  const suffixInputRef = useRef<HTMLInputElement | null>(null);
  const previouslyFocusedRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    previouslyFocusedRef.current = document.activeElement as HTMLElement | null;
    suffixInputRef.current?.focus();
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !submitting) onClose();
    };
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('keydown', onKey);
      previouslyFocusedRef.current?.focus?.();
    };
  }, [onClose, submitting]);

  const onSubmit = async () => {
    setSubmitting(true);
    setFieldError(null);
    setBannerError(null);
    try {
      await createAuction({
        weekId,
        customSuffix: suffix.trim() ? suffix.trim() : undefined,
      });
      onCreated();
    } catch (err: unknown) {
      if (err instanceof DuplicateAuctionTitleError) {
        setFieldError('An auction with this name already exists.');
      } else if (err instanceof AuctionAlreadyExistsError) {
        setBannerError('An auction already exists for this week.');
      } else {
        setBannerError(
          err instanceof Error ? err.message : 'Failed to create auction. Please try again.',
        );
      }
    } finally {
      setSubmitting(false);
    }
  };

  const previewTitle = `${TITLE_PREFIX} ${weekDisplay}${suffix.trim() ? ` ${suffix.trim()}` : ''}`;

  return (
    <div
      className={styles.modalBackdrop}
      role="dialog"
      aria-modal="true"
      aria-labelledby="create-auction-title"
      onMouseDown={e => {
        if (e.target === e.currentTarget && !submitting) onClose();
      }}
    >
      <div className={styles.modal}>
        <h3 id="create-auction-title" className={styles.modalTitle}>Create Auction</h3>

        <p className={styles.helperText}>
          An auction will be created for the selected week. You can add an optional
          suffix to distinguish auctions scheduled for the same week.
        </p>

        <div className={styles.field}>
          <label htmlFor="create-auction-suffix">Auction Name</label>
          <div className={styles.prefixInput}>
            <span className={styles.prefixInputPrefix} aria-hidden="true">
              {TITLE_PREFIX} {weekDisplay}
            </span>
            <input
              id="create-auction-suffix"
              ref={suffixInputRef}
              type="text"
              value={suffix}
              onChange={e => {
                setSuffix(e.target.value);
                if (fieldError) setFieldError(null);
              }}
              placeholder="Optional suffix"
              disabled={submitting}
              aria-describedby="create-auction-preview"
              maxLength={100}
            />
          </div>
          <p id="create-auction-preview" className={styles.previewText}>
            Preview: <strong>{previewTitle}</strong>
          </p>
          {fieldError && <span className={styles.error}>{fieldError}</span>}
        </div>

        <p className={styles.helperText}>
          Three bidding rounds will be scheduled automatically based on the week
          start time. Round start and end times can be adjusted afterwards from the
          auction overview.
        </p>

        {bannerError && (
          <div className={styles.errorBanner} role="alert">
            {bannerError}
          </div>
        )}

        <div className={styles.modalActions}>
          <button
            type="button"
            className={styles.buttonGhost}
            onClick={onClose}
            disabled={submitting}
          >
            Cancel
          </button>
          <button
            type="button"
            className={styles.button}
            onClick={onSubmit}
            disabled={submitting}
          >
            {submitting ? 'Creating…' : 'Create'}
          </button>
        </div>
      </div>
    </div>
  );
}
