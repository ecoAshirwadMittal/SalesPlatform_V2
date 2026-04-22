'use client';

interface EndOfBiddingPanelProps {
  subtitle: string;
  action?: { label: string; onClick: () => void };
}

const FONT_STACK = "'Trebuchet MS', sans-serif";

export function EndOfBiddingPanel({ subtitle, action }: EndOfBiddingPanelProps) {
  return (
    <div
      style={{ fontFamily: FONT_STACK }}
      className="flex min-h-[60vh] flex-col items-center justify-center gap-4 p-6 text-center"
    >
      <h1 className="text-2xl font-bold text-[#112d32]">
        <span className="text-[#2CB34A]">Bidding</span> has ended.
      </h1>
      <p className="text-base text-[#514F4E]">{subtitle}</p>
      {action && (
        <button
          type="button"
          onClick={action.onClick}
          className="mt-2 rounded-full bg-[#407874] px-6 py-2 text-white transition-colors hover:bg-[#325f5c]"
        >
          {action.label}
        </button>
      )}
    </div>
  );
}
