'use client';

import styles from './wizard.module.css';

export type WizardStep = 'overview' | 'missing' | 'wrong' | 'encumbered' | 'summary';

interface Props {
  current: WizardStep;
  hasMissing: boolean;
  hasWrong: boolean;
  hasEncumbered: boolean;
}

interface StepNode {
  key: WizardStep;
  label: string;
}

/**
 * Inline SVG check glyph — replaces the previous UTF-8 check character so
 * the Done state rasterizes consistently regardless of the user's font stack.
 * Sized to ~14px to sit centered inside the 32px green circle.
 */
function CheckIcon() {
  return (
    <svg
      viewBox="0 0 16 16"
      width="14"
      height="14"
      aria-hidden="true"
      focusable="false"
      fill="none"
    >
      <path
        d="M13.5 4.5L6.5 11.5L2.5 7.5"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

/**
 * Renders the Figma "Step Horizontal" indicator. The middle steps appear
 * only when the matching reason flag is true on the current request — when
 * none are set yet (Step 1 entry state), the indicator collapses to a
 * single "Device Details" placeholder as in the Figma initial frame.
 *
 * Group 5 update: a single continuous 1px line runs behind all nodes via
 * the dedicated .stepIndicatorLine element rather than per-segment
 * dividers between sibling nodes.
 */
export function StepIndicator({ current, hasMissing, hasWrong, hasEncumbered }: Props) {
  const nodes: StepNode[] = [{ key: 'overview', label: 'Overview' }];

  const anyReason = hasMissing || hasWrong || hasEncumbered;
  if (!anyReason) {
    nodes.push({ key: 'missing', label: 'Device Details' });
  } else {
    if (hasMissing) nodes.push({ key: 'missing', label: 'Missing Device' });
    if (hasWrong) nodes.push({ key: 'wrong', label: 'Wrong Device' });
    if (hasEncumbered) nodes.push({ key: 'encumbered', label: 'Encumbered Device' });
  }
  nodes.push({ key: 'summary', label: 'Summary' });

  const currentIdx = nodes.findIndex((n) => n.key === current);

  return (
    <div className={styles.stepIndicator} aria-label="Wizard progress">
      <span className={styles.stepIndicatorLine} aria-hidden="true" />
      {nodes.map((node, idx) => {
        const done = idx < currentIdx;
        const active = idx === currentIdx;
        const circleClass = `${styles.stepCircle} ${
          done ? styles.stepCircleDone : active ? styles.stepCircleActive : ''
        }`;
        return (
          <div key={node.key} className={styles.stepNode}>
            <span className={circleClass}>{done ? <CheckIcon /> : idx + 1}</span>
            <span className={`${styles.stepLabel} ${active ? styles.stepLabelActive : ''}`}>
              {node.label}
            </span>
          </div>
        );
      })}
    </div>
  );
}
