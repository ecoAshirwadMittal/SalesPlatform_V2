'use client';

/**
 * Round 2 / Round 3 Selection Rules page — ports Mendix
 * {@code acc_RoundTwoCriteriaPage} + {@code PG_Round3Criteria}. A single
 * form edits the {@code bid_round_selection_filters} row for the given
 * round (2 or 3). Administrator-only write; SalesOps + Administrator can
 * read.
 *
 * The page is opened from the auction scheduling page via a "Selection
 * Rules" link on each of the Round 2 / Round 3 fieldsets (target _blank).
 * Round 3's display name is "Upsell Round" per the 2026-04-20 ADR; we use
 * the numeric round in the URL and the Mendix label in the header.
 */

import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useCallback, useEffect, useMemo, useState } from 'react';
import styles from './round-filters.module.css';
import {
  getRoundFilter,
  updateRoundFilter,
  type BidRoundSelectionFilterRequest,
  type BidRoundSelectionFilterResponse,
  type RegularBuyerInventoryOption,
  type RegularBuyerQualification,
} from '@/lib/auctions';
import { getAuthUser } from '@/lib/session';

const INVENTORY_PATH = '/admin/auctions-data-center/inventory';

interface FormState {
  targetPercent: string;
  targetValue: string;
  totalValueFloor: string;
  mergedGrade1: string;
  mergedGrade2: string;
  mergedGrade3: string;
  stbAllowAllBuyersOverride: boolean;
  stbIncludeAllInventory: boolean;
  regularBuyerQualification: RegularBuyerQualification;
  regularBuyerInventoryOptions: RegularBuyerInventoryOption;
}

const ROUND_LABELS: Record<number, string> = {
  2: 'Round 2 Selection Rules',
  3: 'Upsell Round Selection Rules',
};

function formatNumberForInput(value: number | null): string {
  if (value === null || value === undefined) return '';
  return String(value);
}

function parseNumberInput(value: string): number | null {
  const trimmed = value.trim();
  if (trimmed === '') return null;
  const n = Number(trimmed);
  return Number.isFinite(n) ? n : null;
}

function initialFormFromResponse(r: BidRoundSelectionFilterResponse): FormState {
  return {
    targetPercent: formatNumberForInput(r.targetPercent),
    targetValue: formatNumberForInput(r.targetValue),
    totalValueFloor: formatNumberForInput(r.totalValueFloor),
    mergedGrade1: r.mergedGrade1 ?? '',
    mergedGrade2: r.mergedGrade2 ?? '',
    mergedGrade3: r.mergedGrade3 ?? '',
    stbAllowAllBuyersOverride: r.stbAllowAllBuyersOverride,
    stbIncludeAllInventory: r.stbIncludeAllInventory,
    regularBuyerQualification: r.regularBuyerQualification,
    regularBuyerInventoryOptions: r.regularBuyerInventoryOptions,
  };
}

function toRequest(form: FormState): BidRoundSelectionFilterRequest {
  return {
    targetPercent: parseNumberInput(form.targetPercent),
    targetValue: parseNumberInput(form.targetValue),
    totalValueFloor: parseNumberInput(form.totalValueFloor),
    mergedGrade1: form.mergedGrade1.trim() || null,
    mergedGrade2: form.mergedGrade2.trim() || null,
    mergedGrade3: form.mergedGrade3.trim() || null,
    stbAllowAllBuyersOverride: form.stbAllowAllBuyersOverride,
    stbIncludeAllInventory: form.stbIncludeAllInventory,
    regularBuyerQualification: form.regularBuyerQualification,
    regularBuyerInventoryOptions: form.regularBuyerInventoryOptions,
  };
}

export default function RoundFilterPage() {
  const params = useParams();
  const router = useRouter();
  const rawRound = typeof params?.round === 'string' ? params.round : '';
  const roundNum = Number(rawRound);
  const isValidRound = roundNum === 2 || roundNum === 3;
  const round = (isValidRound ? (roundNum as 2 | 3) : 2);

  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [form, setForm] = useState<FormState | null>(null);
  const [saving, setSaving] = useState(false);
  const [bannerError, setBannerError] = useState<string | null>(null);
  const [bannerSuccess, setBannerSuccess] = useState<string | null>(null);

  const isAdministrator = useMemo(() => {
    const roles = getAuthUser()?.roles ?? [];
    return roles.includes('Administrator');
  }, []);

  const load = useCallback(async () => {
    if (!isValidRound) {
      setLoading(false);
      setLoadError('Invalid round. Use /round-filters/2 or /round-filters/3.');
      return;
    }
    setLoading(true);
    setLoadError(null);
    try {
      const res = await getRoundFilter(round);
      setForm(initialFormFromResponse(res));
    } catch (err) {
      setLoadError(err instanceof Error ? err.message : 'Failed to load selection rules.');
    } finally {
      setLoading(false);
    }
  }, [isValidRound, round]);

  useEffect(() => {
    void load();
  }, [load]);

  const updateField = <K extends keyof FormState>(key: K, value: FormState[K]) => {
    setForm((prev) => (prev ? { ...prev, [key]: value } : prev));
  };

  const handleSave = async () => {
    if (!form) return;
    setBannerError(null);
    setBannerSuccess(null);
    setSaving(true);
    try {
      const updated = await updateRoundFilter(round, toRequest(form));
      setForm(initialFormFromResponse(updated));
      setBannerSuccess('Selection rules saved.');
    } catch (err) {
      setBannerError(err instanceof Error ? err.message : 'Failed to save selection rules.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.page}>
        <div className={styles.loading}>Loading selection rules…</div>
      </div>
    );
  }

  if (loadError || !form) {
    return (
      <div className={styles.page}>
        <div className={styles.bannerError} role="alert">
          {loadError ?? 'Failed to load selection rules.'}
        </div>
        <Link href={INVENTORY_PATH} className={styles.backLink}>
          ← Back to Inventory
        </Link>
      </div>
    );
  }

  const readOnly = !isAdministrator;
  const titleLabel = ROUND_LABELS[round] ?? `Round ${round} Selection Rules`;

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <button
          type="button"
          className={styles.backLink}
          style={{ background: 'none', border: 0, padding: 0, cursor: 'pointer' }}
          onClick={() => router.back()}
        >
          ← Back
        </button>
        <h1 className={styles.title}>{titleLabel}</h1>
        <p className={styles.subtitle}>
          Qualification filters applied when the system selects buyers and inventory for this round.
        </p>
      </header>

      {readOnly && (
        <div className={styles.bannerInfo} role="status">
          Read-only view. Editing the selection rules requires an Administrator role.
        </div>
      )}

      {bannerError && (
        <div className={styles.bannerError} role="alert">
          {bannerError}
        </div>
      )}
      {bannerSuccess && (
        <div className={styles.bannerSuccess} role="status">
          {bannerSuccess}
        </div>
      )}

      <section className={styles.card} aria-labelledby="target-price-factors">
        <h2 id="target-price-factors" className={styles.cardTitle}>Target Price Factors</h2>
        <div className={styles.fieldGrid}>
          <div className={styles.field}>
            <label htmlFor="targetPercent">Target Percent</label>
            <input
              id="targetPercent"
              type="number"
              step="0.0001"
              value={form.targetPercent}
              onChange={(e) => updateField('targetPercent', e.target.value)}
              disabled={readOnly || saving}
            />
            <span className={styles.hint}>e.g. 85 (for 85%)</span>
          </div>
          <div className={styles.field}>
            <label htmlFor="targetValue">Target Value</label>
            <input
              id="targetValue"
              type="number"
              step="0.01"
              value={form.targetValue}
              onChange={(e) => updateField('targetValue', e.target.value)}
              disabled={readOnly || saving}
            />
            <span className={styles.hint}>Dollar threshold for qualification</span>
          </div>
          <div className={styles.field}>
            <label htmlFor="totalValueFloor">Total Value Floor</label>
            <input
              id="totalValueFloor"
              type="number"
              step="0.01"
              value={form.totalValueFloor}
              onChange={(e) => updateField('totalValueFloor', e.target.value)}
              disabled={readOnly || saving}
            />
          </div>
        </div>
      </section>

      <section className={styles.card} aria-labelledby="merged-grades">
        <h2 id="merged-grades" className={styles.cardTitle}>Merged Grades</h2>
        <div className={styles.fieldGrid}>
          <div className={styles.field}>
            <label htmlFor="mergedGrade1">Merged Grade 1</label>
            <input
              id="mergedGrade1"
              type="text"
              value={form.mergedGrade1}
              onChange={(e) => updateField('mergedGrade1', e.target.value)}
              disabled={readOnly || saving}
              maxLength={30}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="mergedGrade2">Merged Grade 2</label>
            <input
              id="mergedGrade2"
              type="text"
              value={form.mergedGrade2}
              onChange={(e) => updateField('mergedGrade2', e.target.value)}
              disabled={readOnly || saving}
              maxLength={30}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="mergedGrade3">Merged Grade 3</label>
            <input
              id="mergedGrade3"
              type="text"
              value={form.mergedGrade3}
              onChange={(e) => updateField('mergedGrade3', e.target.value)}
              disabled={readOnly || saving}
              maxLength={30}
            />
          </div>
        </div>
      </section>

      <section className={styles.card} aria-labelledby="sku-buyer-qualification">
        <h2 id="sku-buyer-qualification" className={styles.cardTitle}>SKU and Buyer Qualification</h2>
        <div className={styles.fieldGrid}>
          <div className={styles.field}>
            <label htmlFor="regularBuyerQualification">Regular Buyer Qualification</label>
            <select
              id="regularBuyerQualification"
              value={form.regularBuyerQualification}
              onChange={(e) =>
                updateField(
                  'regularBuyerQualification',
                  e.target.value as RegularBuyerQualification,
                )
              }
              disabled={readOnly || saving}
            >
              <option value="Only_Qualified">Only qualified buyers</option>
              <option value="All_Buyers">All buyers</option>
            </select>
          </div>
          <div className={styles.field}>
            <label htmlFor="regularBuyerInventoryOptions">Regular Buyer Inventory Options</label>
            <select
              id="regularBuyerInventoryOptions"
              value={form.regularBuyerInventoryOptions}
              onChange={(e) =>
                updateField(
                  'regularBuyerInventoryOptions',
                  e.target.value as RegularBuyerInventoryOption,
                )
              }
              disabled={readOnly || saving}
            >
              <option value="InventoryRound1QualifiedBids">Inventory with Round 1 qualified bids</option>
              <option value="ShowAllInventory">Show all inventory</option>
            </select>
          </div>
          <div className={`${styles.field} ${styles.fieldFull}`}>
            <label className={styles.checkboxRow}>
              <input
                type="checkbox"
                checked={form.stbAllowAllBuyersOverride}
                onChange={(e) => updateField('stbAllowAllBuyersOverride', e.target.checked)}
                disabled={readOnly || saving}
              />
              Special-treatment buyers: allow all-buyers override
            </label>
            <label className={styles.checkboxRow}>
              <input
                type="checkbox"
                checked={form.stbIncludeAllInventory}
                onChange={(e) => updateField('stbIncludeAllInventory', e.target.checked)}
                disabled={readOnly || saving}
              />
              Special-treatment buyers: include all inventory
            </label>
          </div>
        </div>
      </section>

      <div className={styles.actionsRow}>
        <div className={styles.spacer} />
        <button
          type="button"
          className={styles.buttonGhost}
          onClick={() => void load()}
          disabled={saving}
        >
          Reset
        </button>
        {!readOnly && (
          <button
            type="button"
            className={styles.button}
            onClick={() => void handleSave()}
            disabled={saving}
          >
            {saving ? 'Saving…' : 'Save'}
          </button>
        )}
      </div>
    </div>
  );
}
