'use client';

import { useEffect, useState } from 'react';
import styles from './inventory.module.css';
import { fetchWeeks, type WeekOption } from '@/lib/aggregatedInventory';

export default function AggregatedInventoryPage() {
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [selectedWeekId, setSelectedWeekId] = useState<number | null>(null);
  const [lastSyncedAt, setLastSyncedAt] = useState<string | null>(null);

  useEffect(() => {
    fetchWeeks().then(list => {
      setWeeks(list);
      // Mendix parity: default to the first week whose end datetime is in the future.
      const now = Date.now();
      const current = list.find(w => new Date(w.weekEndDateTime).getTime() > now);
      setSelectedWeekId((current ?? list[0])?.id ?? null);
    }).catch(() => setWeeks([]));
  }, []);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>Inventory</h2>
        <select
          className={styles.weekSelect}
          value={selectedWeekId ?? ''}
          onChange={e => setSelectedWeekId(Number(e.target.value))}
        >
          {weeks.map(w => (
            <option key={w.id} value={w.id}>{w.weekDisplay}</option>
          ))}
        </select>
        <button className={styles.button} type="button">Create Auction</button>
        <button className={styles.buttonGhost} type="button">Refresh</button>
        <span className={styles.lastSynced}>
          Last synced: {lastSyncedAt ?? '—'}
        </span>
      </header>

      <section className={styles.kpiStrip} aria-label="Inventory totals">
        {/* Filled in Phase 4 */}
      </section>

      <div className={styles.gridWrap}>
        {/* Filled in Phase 4 */}
      </div>
    </div>
  );
}
