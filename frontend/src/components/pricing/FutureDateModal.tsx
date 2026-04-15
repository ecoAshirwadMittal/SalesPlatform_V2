'use client';

import s from '@/app/pws/pricing/pricing.module.css';

interface FutureDateModalProps {
  value: string;
  saving: boolean;
  onChange: (value: string) => void;
  onSave: () => void;
  onClose: () => void;
}

export function FutureDateModal({ value, saving, onChange, onSave, onClose }: FutureDateModalProps) {
  return (
    <div className={s.modalOverlay} onClick={onClose}>
      <div className={s.modal} onClick={(e) => e.stopPropagation()}>
        <div className={s.modalHeader}>
          <h2 className={s.modalTitle}>Set Future Price Date</h2>
          <button className={s.modalClose} onClick={onClose}>×</button>
        </div>
        <div className={s.modalBody}>
          <p className={s.modalHint}>
            Select the date when future prices become effective.
          </p>
          <input
            type="date"
            className={s.filterInput}
            value={value}
            min={new Date().toISOString().slice(0, 10)}
            onChange={(e) => onChange(e.target.value)}
          />
          <div style={{ marginTop: 16, display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <button className={s.actionBtn} type="button" onClick={onClose}>
              Cancel
            </button>
            <button
              className={`${s.actionBtn} ${s.saveBtn}`}
              type="button"
              onClick={onSave}
              disabled={saving}
            >
              {saving ? 'Saving…' : 'Save'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
