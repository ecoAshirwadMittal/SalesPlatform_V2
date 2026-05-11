import { Suspense } from 'react';
import { MissingDevicesStep } from './MissingDevicesStep';

export default function Page() {
  return (
    <Suspense fallback={<div>Loading…</div>}>
      <MissingDevicesStep />
    </Suspense>
  );
}
