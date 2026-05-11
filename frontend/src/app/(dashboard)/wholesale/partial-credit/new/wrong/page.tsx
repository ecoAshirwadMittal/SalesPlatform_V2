import { Suspense } from 'react';
import { WrongDevicesStep } from './WrongDevicesStep';

export default function Page() {
  return (
    <Suspense fallback={<div>Loading…</div>}>
      <WrongDevicesStep />
    </Suspense>
  );
}
