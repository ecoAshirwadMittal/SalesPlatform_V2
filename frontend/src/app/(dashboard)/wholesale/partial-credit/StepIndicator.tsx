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
 * Renders the Figma "Step Horizontal" indicator. The middle steps appear
 * only when the matching reason flag is true on the current request — when
 * none are set yet (Step 1 entry state), the indicator collapses to a
 * single "Device Details" placeholder as in the Figma initial frame.
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
      {nodes.map((node, idx) => {
        const done = idx < currentIdx;
        const active = idx === currentIdx;
        const circleClass = `${styles.stepCircle} ${
          done ? styles.stepCircleDone : active ? styles.stepCircleActive : ''
        }`;
        return (
          <div key={node.key} style={{ display: 'flex', alignItems: 'center' }}>
            <div className={styles.stepNode}>
              <span className={circleClass}>{done ? '✓' : idx + 1}</span>
              <span className={`${styles.stepLabel} ${active ? styles.stepLabelActive : ''}`}>
                {node.label}
              </span>
            </div>
            {idx < nodes.length - 1 && <span className={styles.stepDivider} />}
          </div>
        );
      })}
    </div>
  );
}
