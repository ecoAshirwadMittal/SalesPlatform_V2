import { Suspense } from 'react';
import { SummaryStep } from './SummaryStep';

export default function Page() {
  return (
    <Suspense fallback={<div>Loading…</div>}>
      <SummaryStep />
    </Suspense>
  );
}
