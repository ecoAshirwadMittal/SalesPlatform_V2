'use client';
import { useEffect, useState } from 'react';
import type {
  SchedulingAuctionSummary,
  BidRoundSummary,
  RoundTimerState,
} from '@/lib/bidder';

interface DashboardHeaderProps {
  auction: SchedulingAuctionSummary;
  bidRound: BidRoundSummary;
  timer: RoundTimerState | null;
}

function formatCountdown(msRemaining: number): string {
  if (msRemaining <= 0) return '00:00:00';
  const totalSeconds = Math.floor(msRemaining / 1000);
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  const pad = (n: number): string => n.toString().padStart(2, '0');
  return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
}

function Countdown({ endsAt }: { endsAt: string }) {
  const [now, setNow] = useState<number>(() => Date.now());

  useEffect(() => {
    const interval = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(interval);
  }, []);

  const msRemaining = new Date(endsAt).getTime() - now;
  return (
    <span className="font-mono text-base tabular-nums">
      {formatCountdown(msRemaining)}
    </span>
  );
}

export function DashboardHeader({ auction, bidRound, timer }: DashboardHeaderProps) {
  const statusIsStarted = bidRound.roundStatus === 'Started';
  const badgeBg = statusIsStarted ? '#407874' : '#9ca3af';
  const showCountdown = timer !== null && timer.active && timer.endsAt !== null;

  return (
    <header className="mb-4 border-b border-[#407874]/30 pb-3">
      <h1 className="text-2xl font-bold" style={{ color: '#407874' }}>
        {auction.auctionTitle}
      </h1>
      <h2 className="mt-2 flex items-center gap-3 text-lg">
        <span className="text-[#112d32]">{auction.roundName}</span>
        <span
          className="rounded-full px-3 py-0.5 text-xs font-semibold uppercase text-white"
          style={{ backgroundColor: badgeBg }}
        >
          {bidRound.roundStatus}
        </span>
        <span className="ml-auto text-sm text-[#112d32]">
          {showCountdown && timer && timer.endsAt !== null ? (
            <>
              <span className="mr-2">Ends in</span>
              <Countdown endsAt={timer.endsAt} />
            </>
          ) : (
            <span className="italic text-gray-500">Round closed</span>
          )}
        </span>
      </h2>
    </header>
  );
}
