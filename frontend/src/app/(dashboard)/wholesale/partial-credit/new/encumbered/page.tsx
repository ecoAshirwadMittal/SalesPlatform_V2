import { Suspense } from 'react';
import { EncumberedDevicesStep } from './EncumberedDevicesStep';

export default function Page() {
  return (
    <Suspense fallback={<div>Loading…</div>}>
      <EncumberedDevicesStep />
    </Suspense>
  );
}
