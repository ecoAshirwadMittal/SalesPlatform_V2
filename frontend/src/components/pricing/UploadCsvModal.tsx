'use client';

import s from '@/app/pws/pricing/pricing.module.css';

export interface UploadResult {
  totalRows: number;
  updatedCount: number;
  errorCount: number;
  errors: string[];
}

interface UploadCsvModalProps {
  uploading: boolean;
  result: UploadResult | null;
  onUpload: (file: File) => void;
  onClose: () => void;
}

export function UploadCsvModal({ uploading, result, onUpload, onClose }: UploadCsvModalProps) {
  return (
    <div className={s.modalOverlay} onClick={onClose}>
      <div className={s.modal} onClick={(e) => e.stopPropagation()}>
        <div className={s.modalHeader}>
          <h2 className={s.modalTitle}>Upload Pricing Data</h2>
          <button className={s.modalClose} onClick={onClose}>×</button>
        </div>
        <div className={s.modalBody}>
          <p className={s.modalHint}>
            CSV format: <code>sku,futureListPrice,futureMinPrice</code>
          </p>
          <input
            type="file"
            accept=".csv"
            className={s.fileInput}
            disabled={uploading}
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) onUpload(file);
            }}
          />
          {uploading && <p className={s.modalStatus}>Uploading…</p>}
          {result && (
            <div className={s.uploadResult}>
              <p><strong>{result.updatedCount}</strong> of {result.totalRows} rows updated</p>
              {result.errorCount > 0 && (
                <div className={s.uploadErrors}>
                  <p><strong>{result.errorCount} errors:</strong></p>
                  <ul>
                    {result.errors.map((err, i) => (
                      <li key={i}>{err}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
