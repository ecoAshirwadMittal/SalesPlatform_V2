// Zod validation for docs/tasks/parity/{config.yaml, pages/*.yaml}.
// v1 implements the load-bearing subset of the full schema in
// docs/tasks/parity-program-plan-2026-07-11.md §4; fields are additive-forward.
import { z } from 'zod';

export const StepSchema = z
  .object({
    click: z.string().optional(),
    fill: z.string().optional(),
    value: z.string().optional(),
    waitFor: z.string().optional(),
    waitMs: z.number().int().positive().max(30_000).optional(),
  })
  .refine((s) => s.click || s.fill || s.waitFor || s.waitMs, {
    message: 'step needs one of click/fill/waitFor/waitMs',
  });
export type Step = z.infer<typeof StepSchema>;

export const MaskSchema = z.object({
  legacy: z.string().optional(),
  new: z.string().optional(),
  reason: z.enum(['DATA', 'TIMESTAMP', 'CDN-IMAGE', 'NATIVE-CONTROL', 'IRREDUCIBLE-AA', 'KNOWN-DEFERRED']),
  note: z.string(),
});

export const StateSchema = z.object({
  id: z.string().regex(/^[a-z0-9-]+$/),
  setup: z.object({ legacy: z.array(StepSchema).optional(), new: z.array(StepSchema).optional() }).optional(),
});

export const PageSchema = z.object({
  pageId: z.string().regex(/^[a-z0-9-]+$/),
  title: z.string(),
  surface: z.enum(['auth', 'buyer', 'admin', 'shell']),
  auth: z.object({ legacy: z.string(), new: z.string() }),
  skip: z.string().optional(),
  legacy: z.object({ path: z.string(), journey: z.array(StepSchema).optional() }),
  new: z.object({ path: z.string(), journey: z.array(StepSchema).optional() }),
  ready: z.object({ legacy: z.string().optional(), new: z.string().optional() }).optional(),
  fullPage: z.boolean().optional(),
  states: z.array(StateSchema).min(1),
  masks: z.array(MaskSchema).default([]),
  findings: z.array(z.string()).default([]),
  status: z.enum(['not-started', 'diffing', 'green', 'signed-off']).default('not-started'),
});
export type ParityPage = z.infer<typeof PageSchema>;

export const ConfigSchema = z.object({
  legacyBase: z.string().url(),
  legacyHostedBase: z.string().url(),
  newBase: z.string().url(),
  viewport: z.object({ width: z.number().int(), height: z.number().int() }),
  fixedTime: z.string(),
  settleMs: z.number().int().default(750),
  legacyLocalHideCss: z.string().default(''),
});
export type ParityConfig = z.infer<typeof ConfigSchema>;
