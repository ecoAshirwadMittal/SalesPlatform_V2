'use client';

/**
 * Auction Scheduling page — port of Mendix `acc_AuctionSchedulePage`.
 *
 * Flow:
 *   1. Load auction detail + schedule defaults in parallel on mount.
 *   2. Render R1 (always editable), R2 (toggle + editable End, read-only
 *      From derived from R1 End + round2MinutesOffset), R3 (toggle +
 *      editable End, read-only From derived from R2 End + round3MinutesOffset).
 *   3. Confirm button opens the summary modal; on "Looks good, Schedule!"
 *      the PUT /schedule endpoint is called and the user is redirected
 *      back to the inventory page.
 *
 * Error mapping (see lib/auctions.ts):
 *   - RoundValidationError → inline per-round messages + banner
 *   - AuctionAlreadyStartedError → banner (immutable once started)
 *   - AuctionHasBidsError → banner (immutable once bids exist)
 *   - AuctionNotFoundError → redirect back to inventory with an error flag
 */

import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useCallback, useEffect, useMemo, useState } from 'react';
import styles from './schedule.module.css';
import { ConfirmActionModal } from './ConfirmActionModal';
import { ConfirmModal } from './ConfirmModal';
import { RoundFieldset } from './RoundFieldset';
import {
  ROUND_LABELS,
  addMinutes,
  initialStateFromDefaults,
  isoFromFields,
  isoToFields,
  mapDetailsToRoundErrors,
  splitIso,
  validateForm,
  type FormState,
  type RoundErrors,
  type RoundFields,
} from './schedule-form';
import {
  AuctionAlreadyStartedError,
  AuctionHasBidsError,
  AuctionNotFoundError,
  RoundValidationError,
  type AuctionDetailResponse,
  type ScheduleAuctionRequest,
  type ScheduleDefaultsResponse,
  deleteAuction,
  getAuctionDetail,
  getScheduleDefaults,
  saveSchedule,
  unscheduleAuction,
} from '@/lib/auctions';
import { getAuthUser } from '@/lib/session';

const INVENTORY_PATH = '/admin/auctions-data-center/inventory';

export default function AuctionSchedulePage() {
  const params = useParams();
  const router = useRouter();
  const rawId = typeof params?.auctionId === 'string' ? params.auctionId : '';
  const auctionId = Number(rawId);
  const isValidId = Number.isInteger(auctionId) && auctionId > 0;

  const [detail, setDetail] = useState<AuctionDetailResponse | null>(null);
  const [defaults, setDefaults] = useState<ScheduleDefaultsResponse | null>(null);
  const [form, setForm] = useState<FormState | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);

  const [bannerError, setBannerError] = useState<string | null>(null);
  const [roundErrors, setRoundErrors] = useState<RoundErrors>({});

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [unscheduleConfirm, setUnscheduleConfirm] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState(false);
  const [unscheduling, setUnscheduling] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const isAdministrator = useMemo(() => {
    const roles = getAuthUser()?.roles ?? [];
    return roles.includes('Administrator');
  }, []);

  const loadAll = useCallback(async () => {
    if (!isValidId) {
      setLoading(false);
      setLoadError('Invalid auction id.');
      return;
    }
    setLoading(true);
    setLoadError(null);
    try {
      const [d, sd] = await Promise.all([
        getAuctionDetail(auctionId),
        getScheduleDefaults(auctionId),
      ]);
      setDetail(d);
      setDefaults(sd);
      setForm(initialStateFromDefaults(sd));
    } catch (err) {
      if (err instanceof AuctionNotFoundError) {
        router.replace(`${INVENTORY_PATH}?error=auction-not-found`);
        return;
      }
      setLoadError(err instanceof Error ? err.message : 'Failed to load auction schedule.');
    } finally {
      setLoading(false);
    }
  }, [auctionId, isValidId, router]);

  useEffect(() => {
    void loadAll();
  }, [loadAll]);

  /**
   * Recompute R2 From whenever R1 To changes (and R2 is active). Mirrors
   * the Mendix on-change cascade — prevents the admin from having to
   * manually bump the R2 From field after shifting R1.
   */
  useEffect(() => {
    if (!form || !defaults) return;
    const r1EndIso = isoToFields(form.round1);
    if (!r1EndIso) return;
    const r2StartIso = addMinutes(r1EndIso, defaults.round2MinutesOffset);
    if (!r2StartIso) return;
    const split = splitIso(r2StartIso);
    setForm((prev) => {
      if (!prev) return prev;
      if (
        prev.round2.fromDate === split.date &&
        prev.round2.fromTime === split.time
      ) {
        return prev;
      }
      return {
        ...prev,
        round2: { ...prev.round2, fromDate: split.date, fromTime: split.time },
      };
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [form?.round1.toDate, form?.round1.toTime, defaults?.round2MinutesOffset]);

  /**
   * Cascade R3 From from R2 To + configured offset.
   */
  useEffect(() => {
    if (!form || !defaults) return;
    const r2EndIso = isoToFields(form.round2);
    if (!r2EndIso) return;
    const r3StartIso = addMinutes(r2EndIso, defaults.round3MinutesOffset);
    if (!r3StartIso) return;
    const split = splitIso(r3StartIso);
    setForm((prev) => {
      if (!prev) return prev;
      if (
        prev.round3.fromDate === split.date &&
        prev.round3.fromTime === split.time
      ) {
        return prev;
      }
      return {
        ...prev,
        round3: { ...prev.round3, fromDate: split.date, fromTime: split.time },
      };
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [form?.round2.toDate, form?.round2.toTime, defaults?.round3MinutesOffset]);

  const updateRoundField = <K extends keyof RoundFields>(
    round: 'round1' | 'round2' | 'round3',
    field: K,
    value: string,
  ) => {
    setForm((prev) => {
      if (!prev) return prev;
      return { ...prev, [round]: { ...prev[round], [field]: value } };
    });
  };

  const setActive = (round: 'round2' | 'round3', active: boolean) => {
    setForm((prev) => {
      if (!prev) return prev;
      return round === 'round2'
        ? { ...prev, round2Active: active }
        : { ...prev, round3Active: active };
    });
  };

  const openConfirm = () => {
    setBannerError(null);
    if (!form) return;
    const errs = validateForm(form);
    setRoundErrors(errs);
    if (Object.keys(errs).length > 0) {
      setBannerError('Please fix the highlighted round(s) before confirming.');
      return;
    }
    setConfirmOpen(true);
  };

  const confirmSchedule = async () => {
    if (!form) return;
    setSubmitting(true);
    setBannerError(null);
    try {
      const r1Start = isoFromFields(form.round1);
      const r1End = isoToFields(form.round1);
      const r2Start = isoFromFields(form.round2);
      const r2End = isoToFields(form.round2);
      const r3Start = isoFromFields(form.round3);
      const r3End = isoToFields(form.round3);
      if (!r1Start || !r1End || !r2Start || !r2End || !r3Start || !r3End) {
        setBannerError('All round times must be filled in before scheduling.');
        setConfirmOpen(false);
        return;
      }
      const req: ScheduleAuctionRequest = {
        round1Start: r1Start,
        round1End: r1End,
        round2Start: r2Start,
        round2End: r2End,
        round2Active: form.round2Active,
        round3Start: r3Start,
        round3End: r3End,
        round3Active: form.round3Active,
      };
      await saveSchedule(auctionId, req);
      router.push(INVENTORY_PATH);
    } catch (err) {
      setConfirmOpen(false);
      if (err instanceof RoundValidationError) {
        setRoundErrors(mapDetailsToRoundErrors(err.details.length > 0 ? err.details : [err.message]));
        setBannerError(err.message);
      } else if (err instanceof AuctionAlreadyStartedError) {
        setBannerError('This auction has already started. The schedule cannot be changed.');
      } else if (err instanceof AuctionHasBidsError) {
        setBannerError('Bids have been submitted. The schedule cannot be changed.');
      } else if (err instanceof AuctionNotFoundError) {
        router.replace(`${INVENTORY_PATH}?error=auction-not-found`);
      } else {
        setBannerError(err instanceof Error ? err.message : 'Failed to save schedule.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleUnschedule = async () => {
    setUnscheduleConfirm(false);
    setBannerError(null);
    setUnscheduling(true);
    try {
      await unscheduleAuction(auctionId);
      await loadAll();
    } catch (err) {
      if (err instanceof AuctionAlreadyStartedError) {
        setBannerError('This auction has already started. It cannot be unscheduled.');
      } else if (err instanceof AuctionNotFoundError) {
        router.replace(`${INVENTORY_PATH}?error=auction-not-found`);
      } else {
        setBannerError(err instanceof Error ? err.message : 'Failed to unschedule.');
      }
    } finally {
      setUnscheduling(false);
    }
  };

  const handleDelete = async () => {
    setDeleteConfirm(false);
    setBannerError(null);
    setDeleting(true);
    try {
      await deleteAuction(auctionId);
      router.push(INVENTORY_PATH);
    } catch (err) {
      if (err instanceof AuctionAlreadyStartedError) {
        setBannerError('This auction has already started. It cannot be deleted.');
      } else if (err instanceof AuctionNotFoundError) {
        router.replace(`${INVENTORY_PATH}?error=auction-not-found`);
      } else {
        setBannerError(err instanceof Error ? err.message : 'Failed to delete auction.');
      }
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.page}>
        <div className={styles.loading}>Loading auction schedule…</div>
      </div>
    );
  }

  if (loadError || !detail || !form || !defaults) {
    return (
      <div className={styles.page}>
        <div className={styles.bannerError}>{loadError ?? 'Failed to load auction schedule.'}</div>
        <Link href={INVENTORY_PATH} className={styles.backLink}>
          ← Back to Inventory
        </Link>
      </div>
    );
  }

  const isScheduled = detail.auctionStatus === 'Scheduled';
  // Any started round locks the whole schedule; treat this as read-only.
  const isStarted =
    detail.auctionStatus === 'Started' ||
    detail.rounds.some((r) => r.roundStatus === 'Started');
  const formDisabled = submitting || unscheduling || deleting || isStarted;

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <div style={{ marginRight: 'auto' }}>
          <Link href={INVENTORY_PATH} className={styles.backLink}>
            ← Back to Inventory
          </Link>
          <h1 className={styles.title}>{detail.auctionTitle}</h1>
          <p className={styles.subtitle}>
            {detail.weekDisplay}
            {' · '}
            <span
              className={`${styles.statusPill} ${
                detail.auctionStatus === 'Unscheduled' ? styles.statusPillMuted : ''
              }`}
            >
              {detail.auctionStatus}
            </span>
          </p>
        </div>
      </header>

      {isStarted && (
        <div className={styles.bannerInfo} role="status">
          This auction has already started. The schedule cannot be changed.
        </div>
      )}

      {bannerError && (
        <div className={styles.bannerError} role="alert">
          {bannerError}
        </div>
      )}

      <RoundFieldset
        title={ROUND_LABELS[1]}
        fields={form.round1}
        onChange={(field, value) => updateRoundField('round1', field, value)}
        disabled={formDisabled}
        error={roundErrors.round1}
      />

      <RoundFieldset
        title={ROUND_LABELS[2]}
        fields={form.round2}
        onChange={(field, value) => {
          if (field === 'fromDate' || field === 'fromTime') return;
          updateRoundField('round2', field, value);
        }}
        disabled={formDisabled || !form.round2Active}
        fromReadOnly
        error={roundErrors.round2}
        toggle={{
          label: 'Round 2 active',
          checked: form.round2Active,
          onChange: (v) => setActive('round2', v),
          disabled: formDisabled,
        }}
        selectionRulesHref="/admin/auctions-data-center/auctions/round-filters/2"
      />

      <RoundFieldset
        title={ROUND_LABELS[3]}
        fields={form.round3}
        onChange={(field, value) => {
          if (field === 'fromDate' || field === 'fromTime') return;
          updateRoundField('round3', field, value);
        }}
        disabled={formDisabled || !form.round3Active}
        fromReadOnly
        error={roundErrors.round3}
        toggle={{
          label: 'Upsell round active',
          checked: form.round3Active,
          onChange: (v) => setActive('round3', v),
          disabled: formDisabled,
        }}
        selectionRulesHref="/admin/auctions-data-center/auctions/round-filters/3"
      />

      <div className={styles.actionsRow}>
        {isAdministrator && (
          <button
            type="button"
            className={styles.buttonDanger}
            onClick={() => setDeleteConfirm(true)}
            disabled={formDisabled || isStarted}
          >
            {deleting ? 'Deleting…' : 'Delete Auction'}
          </button>
        )}
        {isScheduled && !isStarted && (
          <button
            type="button"
            className={styles.buttonGhost}
            onClick={() => setUnscheduleConfirm(true)}
            disabled={formDisabled}
          >
            {unscheduling ? 'Unscheduling…' : 'Unschedule'}
          </button>
        )}
        <div className={styles.spacer} />
        <Link href={INVENTORY_PATH} className={styles.buttonGhost}>
          Cancel
        </Link>
        <button
          type="button"
          className={styles.button}
          onClick={openConfirm}
          disabled={formDisabled}
        >
          Confirm
        </button>
      </div>

      {confirmOpen && form && (
        <ConfirmModal
          form={form}
          onCancel={() => setConfirmOpen(false)}
          onConfirm={confirmSchedule}
          submitting={submitting}
        />
      )}

      {unscheduleConfirm && (
        <ConfirmActionModal
          title="Unschedule auction?"
          body="The three rounds will be removed and the auction will go back to Unscheduled. You can reschedule it again at any time."
          confirmLabel="Unschedule"
          confirmVariant="danger"
          onCancel={() => setUnscheduleConfirm(false)}
          onConfirm={handleUnschedule}
          submitting={unscheduling}
        />
      )}

      {deleteConfirm && (
        <ConfirmActionModal
          title="Delete auction?"
          body={`This will permanently delete "${detail.auctionTitle}" and all of its scheduling data. This action cannot be undone.`}
          confirmLabel="Delete"
          confirmVariant="danger"
          warning="Bid rounds and related data will be removed."
          onCancel={() => setDeleteConfirm(false)}
          onConfirm={handleDelete}
          submitting={deleting}
        />
      )}
    </div>
  );
}
