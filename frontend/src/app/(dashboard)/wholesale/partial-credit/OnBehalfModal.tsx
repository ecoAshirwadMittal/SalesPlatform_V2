'use client';

import { useEffect, useState } from 'react';
import {
  createDraftOnBehalf,
  listBuyersForCode,
  listEligibleBuyerCodes,
  type BuyerCodeOption,
  type BuyerUserOption,
} from '@/lib/onBehalfPartialCreditClient';
import styles from './onBehalfModal.module.css';

interface OnBehalfModalProps {
  open: boolean;
  onClose: () => void;
  /** Fired after the draft is created — the parent navigates the rep
   *  into the standard wizard. */
  onDraftCreated: (draftId: number) => void;
}

type Step = 'CODE' | 'USER' | 'ORDER';

/**
 * Three-step "Submit on behalf of a buyer" modal:
 *  1. CODE — rep picks one of the buyer codes they can file for.
 *  2. USER — rep picks the buyer user (from the picked code's company).
 *  3. ORDER — rep types the order number and hits Create.
 *
 * Confirm on the ORDER step triggers
 * {@code createDraftOnBehalf(orderNumber, buyerCodeId, onBehalfOfUserId)}
 * and the parent navigates to the wizard step 1 with the returned id.
 *
 * Picker fetches are lazy (CODE on open, USER on code-selection) so the
 * modal does not stall on the buyer-user list when the rep is still
 * deciding which code to pick.
 */
export function OnBehalfModal({ open, onClose, onDraftCreated }: OnBehalfModalProps) {
  const [step, setStep] = useState<Step>('CODE');
  const [codes, setCodes] = useState<BuyerCodeOption[] | null>(null);
  const [users, setUsers] = useState<BuyerUserOption[] | null>(null);
  const [selectedCode, setSelectedCode] = useState<BuyerCodeOption | null>(null);
  const [selectedUser, setSelectedUser] = useState<BuyerUserOption | null>(null);
  const [orderNumber, setOrderNumber] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) {
      // Reset state every time the modal is reopened so a stale list
      // never bleeds across two on-behalf sessions.
      setStep('CODE');
      setCodes(null);
      setUsers(null);
      setSelectedCode(null);
      setSelectedUser(null);
      setOrderNumber('');
      setError(null);
      return;
    }
    void (async () => {
      setLoading(true);
      setError(null);
      try {
        setCodes(await listEligibleBuyerCodes());
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load buyer codes');
      } finally {
        setLoading(false);
      }
    })();
  }, [open]);

  async function pickCode(code: BuyerCodeOption) {
    setSelectedCode(code);
    setStep('USER');
    setLoading(true);
    setError(null);
    try {
      setUsers(await listBuyersForCode(code.id));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load buyer users');
    } finally {
      setLoading(false);
    }
  }

  function pickUser(user: BuyerUserOption) {
    setSelectedUser(user);
    setStep('ORDER');
  }

  async function confirm() {
    if (!selectedCode || !selectedUser || !orderNumber.trim()) return;
    setSubmitting(true);
    setError(null);
    try {
      const detail = await createDraftOnBehalf(
        orderNumber.trim(),
        selectedCode.id,
        selectedUser.userId,
      );
      onDraftCreated(detail.id);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create draft');
    } finally {
      setSubmitting(false);
    }
  }

  if (!open) return null;

  return (
    <div className={styles.backdrop} role="dialog" aria-label="Submit on behalf">
      <div className={styles.modal}>
        <header className={styles.header}>
          <h2 className={styles.heading}>Submit on behalf of a buyer</h2>
          <button
            type="button"
            className={styles.closeButton}
            onClick={onClose}
            aria-label="Close"
          >
            ✕
          </button>
        </header>

        <Steps current={step} />

        {error && (
          <div className={styles.error} role="alert">
            {error}
          </div>
        )}

        {step === 'CODE' && (
          <Picker
            label="Pick a buyer code"
            loading={loading}
            items={codes ?? []}
            renderItem={(c) => (
              <>
                <span className={styles.itemPrimary}>{c.code}</span>
                <span className={styles.itemSecondary}>{c.buyerName ?? '—'}</span>
              </>
            )}
            onSelect={pickCode}
            emptyMessage="No buyer codes available."
          />
        )}

        {step === 'USER' && (
          <Picker
            label={`Pick a buyer user (code ${selectedCode?.code ?? ''})`}
            loading={loading}
            items={users ?? []}
            renderItem={(u) => (
              <>
                <span className={styles.itemPrimary}>{u.displayName}</span>
                <span className={styles.itemSecondary}>{u.email ?? ''}</span>
              </>
            )}
            onSelect={pickUser}
            emptyMessage="No buyer users associated with this code."
          />
        )}

        {step === 'ORDER' && (
          <div className={styles.orderStep}>
            <p className={styles.orderSummary}>
              Submitting on behalf of <strong>{selectedUser?.displayName}</strong>
              {' for code '}
              <strong>{selectedCode?.code}</strong>.
            </p>
            <label className={styles.orderField}>
              <span className={styles.fieldLabel}>Order number</span>
              <input
                type="text"
                className={styles.orderInput}
                value={orderNumber}
                onChange={(e) => setOrderNumber(e.target.value)}
                placeholder="e.g. SO-12345"
                autoFocus
              />
            </label>
            <div className={styles.actions}>
              <button
                type="button"
                className={styles.buttonGhost}
                onClick={() => setStep('USER')}
                disabled={submitting}
              >
                Back
              </button>
              <button
                type="button"
                className={styles.buttonPrimary}
                onClick={confirm}
                disabled={!orderNumber.trim() || submitting}
              >
                {submitting ? 'Creating…' : 'Create draft'}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

interface PickerProps<T> {
  label: string;
  loading: boolean;
  items: T[];
  renderItem: (item: T) => React.ReactNode;
  onSelect: (item: T) => void;
  emptyMessage: string;
}

function Picker<T>({
  label,
  loading,
  items,
  renderItem,
  onSelect,
  emptyMessage,
}: PickerProps<T>) {
  return (
    <div className={styles.picker}>
      <span className={styles.pickerLabel}>{label}</span>
      {loading && <div className={styles.notice}>Loading…</div>}
      {!loading && items.length === 0 && (
        <div className={styles.notice}>{emptyMessage}</div>
      )}
      {!loading && items.length > 0 && (
        <ul className={styles.pickerList} role="listbox">
          {items.map((item, idx) => (
            <li key={idx}>
              <button
                type="button"
                className={styles.pickerItem}
                onClick={() => onSelect(item)}
                role="option"
                aria-selected={false}
              >
                {renderItem(item)}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

interface StepsProps {
  current: Step;
}

function Steps({ current }: StepsProps) {
  const order: Step[] = ['CODE', 'USER', 'ORDER'];
  const idx = order.indexOf(current);
  const labels: Record<Step, string> = {
    CODE: 'Buyer code',
    USER: 'Buyer user',
    ORDER: 'Order number',
  };
  return (
    <ol className={styles.steps} aria-label="Modal progress">
      {order.map((s, i) => (
        <li
          key={s}
          className={
            i === idx
              ? styles.stepCurrent
              : i < idx
                ? styles.stepDone
                : styles.stepPending
          }
        >
          <span className={styles.stepNum}>{i + 1}</span>
          <span>{labels[s]}</span>
        </li>
      ))}
    </ol>
  );
}
