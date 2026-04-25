'use client';

/**
 * R2 Selection Criteria — Lane 4 admin surface.
 *
 * Ports the QA POM {@code ACC_RoundTwoCriteriaPage.selectRegularBuyerSettings}
 * via three controls:
 *   1. Regular Buyer Qualification (radio: Bid Buyers Only | All Buyers)
 *   2. Regular Buyer Inventory Options (radio: Inventory With Bids | Full Inventory)
 *   3. STB Allow All Buyers Override (toggle: Yes | No)
 *
 * Loads the persisted criteria for round 2 on mount; shows defaults when the
 * row hasn't been saved yet (the GET endpoint returns 404 in that case).
 * Save upserts via PUT, returning the projection the page re-syncs from.
 *
 * Cascade tests (criteria → R1 closes → buyers eligible per criteria) are
 * deferred — they require Lane 3B (Qualified Buyer Codes admin) for the
 * end-to-end flow. See docs/tasks/p8-admin-surfaces-plan.md §3 Lane 4.
 */

import Link from 'next/link';
import { useCallback, useEffect, useState, type CSSProperties } from 'react';
import {
  getRoundCriteria,
  updateRoundCriteria,
  ROUND_CRITERIA_DEFAULTS,
  ROUND_CRITERIA_NOT_FOUND,
  type RoundCriteriaInventory,
  type RoundCriteriaQualification,
  type RoundCriteriaResponse,
} from '@/lib/admin/roundCriteria';

const ROUND: 2 = 2;

const pageContainerStyle: CSSProperties = {
  maxWidth: 720,
  margin: '0 auto',
  padding: '32px 24px',
  fontFamily:
    "'Brandon Grotesque', 'Open Sans', system-ui, -apple-system, Arial, sans-serif",
  color: '#102e33',
};

const backLinkStyle: CSSProperties = {
  display: 'inline-block',
  marginBottom: 8,
  color: '#407874',
  textDecoration: 'none',
  fontSize: 13,
};

interface FormState {
  regularBuyerQualification: RoundCriteriaQualification;
  regularBuyerInventoryOptions: RoundCriteriaInventory;
  stbAllowAllBuyersOverride: boolean;
}

function toForm(
  source: RoundCriteriaResponse | typeof ROUND_CRITERIA_DEFAULTS,
): FormState {
  return {
    regularBuyerQualification: source.regularBuyerQualification,
    regularBuyerInventoryOptions: source.regularBuyerInventoryOptions,
    stbAllowAllBuyersOverride: source.stbAllowAllBuyersOverride,
  };
}

export default function R2CriteriaPage() {
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [form, setForm] = useState<FormState>(toForm(ROUND_CRITERIA_DEFAULTS));
  const [persisted, setPersisted] = useState(false);
  const [saving, setSaving] = useState(false);
  const [bannerError, setBannerError] = useState<string | null>(null);
  const [bannerSuccess, setBannerSuccess] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError(null);
    try {
      const res = await getRoundCriteria(ROUND);
      if (res === ROUND_CRITERIA_NOT_FOUND) {
        // No row yet — show defaults locally; first Save creates the row.
        setForm(toForm(ROUND_CRITERIA_DEFAULTS));
        setPersisted(false);
      } else {
        setForm(toForm(res));
        setPersisted(true);
      }
    } catch (err) {
      setLoadError(err instanceof Error ? err.message : 'Failed to load criteria.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const handleSave = async () => {
    setBannerError(null);
    setBannerSuccess(null);
    setSaving(true);
    try {
      const updated = await updateRoundCriteria(ROUND, form);
      setForm(toForm(updated));
      setPersisted(true);
      setBannerSuccess('Selection criteria saved.');
    } catch (err) {
      setBannerError(err instanceof Error ? err.message : 'Failed to save criteria.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div style={pageContainerStyle} data-testid="r2-criteria-loading">
        <p>Loading round 2 selection criteria…</p>
      </div>
    );
  }

  if (loadError) {
    return (
      <div style={pageContainerStyle}>
        <div role="alert" data-testid="r2-criteria-load-error">
          {loadError}
        </div>
        <Link href="/admin/auction-control-center" style={backLinkStyle}>
          ← Back to Auction Control Center
        </Link>
      </div>
    );
  }

  return (
    <div style={pageContainerStyle} data-testid="r2-criteria-page">
      <header style={{ marginBottom: 24 }}>
        <Link href="/admin/auction-control-center" style={backLinkStyle}>
          ← Back to Auction Control Center
        </Link>
        <h1 style={{ fontSize: 24, fontWeight: 600, color: '#102e33', margin: '12px 0 4px' }}>
          Round 2 Selection Criteria
        </h1>
        <p style={{ fontSize: 14, color: '#555', margin: 0 }}>
          Controls which buyers and inventory feed into Round 2 after Round 1 closes.
        </p>
        {!persisted && (
          <p
            data-testid="r2-criteria-defaults-notice"
            style={{
              fontSize: 13,
              color: '#7a4f00',
              background: '#fff3d6',
              padding: '8px 12px',
              borderRadius: 4,
              marginTop: 12,
            }}
          >
            No saved criteria yet — showing defaults. Click Save to persist.
          </p>
        )}
      </header>

      {bannerError && (
        <div
          role="alert"
          data-testid="r2-criteria-error-banner"
          style={{
            background: '#fde2e2',
            color: '#7a1f1f',
            padding: '10px 14px',
            borderRadius: 4,
            marginBottom: 16,
          }}
        >
          {bannerError}
        </div>
      )}
      {bannerSuccess && (
        <div
          role="status"
          data-testid="r2-criteria-success-banner"
          style={{
            background: '#dff6e1',
            color: '#1f5f25',
            padding: '10px 14px',
            borderRadius: 4,
            marginBottom: 16,
          }}
        >
          {bannerSuccess}
        </div>
      )}

      <fieldset
        style={{ border: '1px solid #d8dee2', padding: 16, borderRadius: 6, marginBottom: 20 }}
      >
        <legend style={{ fontWeight: 600, padding: '0 8px' }}>Regular Buyer Qualification</legend>
        <label style={{ display: 'block', marginBottom: 8 }}>
          <input
            type="radio"
            name="regularBuyerQualification"
            value="Bid_Buyers_Only"
            checked={form.regularBuyerQualification === 'Bid_Buyers_Only'}
            onChange={() =>
              setForm((prev) => ({ ...prev, regularBuyerQualification: 'Bid_Buyers_Only' }))
            }
            disabled={saving}
            data-testid="r2-qualification-bid-buyers-only"
          />
          <span style={{ marginLeft: 8 }}>Bid Buyers Only</span>
        </label>
        <label style={{ display: 'block' }}>
          <input
            type="radio"
            name="regularBuyerQualification"
            value="All_Buyers"
            checked={form.regularBuyerQualification === 'All_Buyers'}
            onChange={() =>
              setForm((prev) => ({ ...prev, regularBuyerQualification: 'All_Buyers' }))
            }
            disabled={saving}
            data-testid="r2-qualification-all-buyers"
          />
          <span style={{ marginLeft: 8 }}>All Buyers</span>
        </label>
      </fieldset>

      <fieldset
        style={{ border: '1px solid #d8dee2', padding: 16, borderRadius: 6, marginBottom: 20 }}
      >
        <legend style={{ fontWeight: 600, padding: '0 8px' }}>
          Regular Buyer Inventory Options
        </legend>
        <label style={{ display: 'block', marginBottom: 8 }}>
          <input
            type="radio"
            name="regularBuyerInventoryOptions"
            value="Inventory_With_Bids"
            checked={form.regularBuyerInventoryOptions === 'Inventory_With_Bids'}
            onChange={() =>
              setForm((prev) => ({
                ...prev,
                regularBuyerInventoryOptions: 'Inventory_With_Bids',
              }))
            }
            disabled={saving}
            data-testid="r2-inventory-with-bids"
          />
          <span style={{ marginLeft: 8 }}>Inventory With Bids</span>
        </label>
        <label style={{ display: 'block' }}>
          <input
            type="radio"
            name="regularBuyerInventoryOptions"
            value="Full_Inventory"
            checked={form.regularBuyerInventoryOptions === 'Full_Inventory'}
            onChange={() =>
              setForm((prev) => ({
                ...prev,
                regularBuyerInventoryOptions: 'Full_Inventory',
              }))
            }
            disabled={saving}
            data-testid="r2-inventory-full"
          />
          <span style={{ marginLeft: 8 }}>Full Inventory</span>
        </label>
      </fieldset>

      <fieldset
        style={{ border: '1px solid #d8dee2', padding: 16, borderRadius: 6, marginBottom: 20 }}
      >
        <legend style={{ fontWeight: 600, padding: '0 8px' }}>
          Special-Treatment Buyer: Allow All-Buyers Override
        </legend>
        <label style={{ display: 'inline-flex', alignItems: 'center', gap: 12 }}>
          <input
            type="checkbox"
            checked={form.stbAllowAllBuyersOverride}
            onChange={(e) =>
              setForm((prev) => ({ ...prev, stbAllowAllBuyersOverride: e.target.checked }))
            }
            disabled={saving}
            data-testid="r2-stb-override-toggle"
          />
          <span>{form.stbAllowAllBuyersOverride ? 'Yes' : 'No'}</span>
        </label>
      </fieldset>

      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
        <button
          type="button"
          onClick={() => void load()}
          disabled={saving}
          data-testid="r2-criteria-reset"
          style={{
            padding: '8px 16px',
            borderRadius: 4,
            border: '1px solid #c0c8cc',
            background: '#fff',
            cursor: saving ? 'not-allowed' : 'pointer',
          }}
        >
          Reset
        </button>
        <button
          type="button"
          onClick={() => void handleSave()}
          disabled={saving}
          data-testid="r2-criteria-save"
          style={{
            padding: '8px 16px',
            borderRadius: 4,
            border: '1px solid #102e33',
            background: '#407874',
            color: '#fff',
            cursor: saving ? 'wait' : 'pointer',
          }}
        >
          {saving ? 'Saving…' : 'Save'}
        </button>
      </div>
    </div>
  );
}
