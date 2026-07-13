# Task 2.3 Chunks C+D — Counter-offer reminder templates + scheduled sender — REPORT

**STATUS:** DONE (green)

**Branch:** `worktree-agent-adaf624b3e2494dcb`

**Commits (this task, on top of `main` @ `5c98e644`):**
- `a41637f3` feat(pws): V105 seed counter-offer reminder templates
- `e0980d03` feat(pws): counter-offer reminder scheduled sender (2.3 sub-feature 1)
- `5c9af2fa` docs(pws): document counter-offer reminder cron (2.3 sub-feature 1)
- (this report is the final commit on top)

**V-version used:** **V105** (`V105__seed_pws_counter_offer_reminder_templates.sql`).
Verified free across ALL refs (`git log --all` — no V104 or V105 existed
anywhere). Highest on `main` is V103; Chunk B's V104 is a sibling still in
flight (not yet on any ref), so V105 is the correct next per the brief.

**Test summary:** `CounterOfferReminderServiceTest` 12 + `V105MigrationIT` 2 +
`CounterOfferReminderEndToEndIT` 2 = **16/16 green**
(`-Dspring.profiles.active=pg-test`, real Postgres).

Run:
```
./mvnw test -Dtest=CounterOfferReminderServiceTest,V105MigrationIT,CounterOfferReminderEndToEndIT -Dspring.profiles.active=pg-test
...
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**What shipped:**
- **V105 migration** — two idempotent (`ON CONFLICT (template_key) DO NOTHING`)
  enabled `email.template` rows: `PwsCounterOfferFirstReminder` +
  `PwsCounterOfferSecondReminder`. `{{var}}` placeholders
  (`buyerName`/`companyName`/`offerNumber`/`counterOfferUrl`), no dollar-brace.
  Body copy is **best-effort** (the `SUB_SendFirstReminderEmail` /
  `SUB_SendSecondReminderEmail` bodies are not in `migration_context`).
- **`service/pws/CounterOfferReminderService`** —
  `@Scheduled(fixedDelayString="${pws.counter-reminder.fixed-delay-ms:3600000}")`
  (hourly) + `@SchedulerLock(name="pwsCounterOfferReminder")` + injected `Clock`
  + `@Value("${pws.counter-reminder.enabled:false}")` gate. Scheduled
  `sendCounterOfferReminders()` delegates to public gate-free `runOnce()`
  (mirrors `RmaDeposcoSyncService`). Faithful legacy decision tree: SECOND takes
  precedence (`hours>=hours_second && !second_reminder_sent`, only when
  `send_second_reminder`); FIRST windowed by `hours_second` when present, else
  open-ended (only when `send_first_reminder && !first_reminder_sent`). One-shot
  via the V103 flags; per-row try/catch isolation; buyer-only recipients via
  `EcoATMDirectUserRepository.findActiveEmailsByBuyerCodeId`; dispatch via
  `EmailService.sendTemplated(key, vars, SendOverrides(recipients,null,null),
  SourceRef("PWS_COUNTER_REMINDER", offerId))`.
- **`service/pws/PwsConstantsReader`** + **`PwsCounterReminderSettings`** record
  — reads the `pws.pws_constants` singleton via `JdbcTemplate` (matching
  `PWSAdminController.getPWSConstants`; no JPA entity added). Fail-safe: absent
  row → send nothing.
- **`OfferRepository.findCounterReminderCandidates`** — the
  `[Buyer_Acceptance] [first OR second unsent]` finder.
- **Config** in `application.yml` (`pws.counter-reminder.{enabled:false,
  fixed-delay-ms:3600000}`) + docs (`modules.md`, `deployment/setup.md`,
  `testing/coverage.md`). Did **not** touch `docs/architecture/decisions.md`.

**Concerns / notes:**
1. **Worktree was stale.** Cut at `17827f21` (highest migration V100); current
   `main` was `5c98e644` (V103, Chunk A merged). `17827f21` is a clean ancestor
   with zero local commits, so I `--ff-only`-advanced the worktree to `main`
   first (same move Chunk A's report documents), so the V103 `Offer` reminder
   flags this task depends on exist locally. Worth confirming the orchestrator
   expected the worktree to already be at current main.
2. **V104 is absent from my tree** (Chunk B, in flight). Flyway applies V103 →
   V105 with the gap; `pg-test` has `out-of-order: true` so this is fine on the
   shared dev DB (currently at V103) and on a fresh CI DB (V104 will slot in
   before V105 when both merge). No collision — V105 verified free everywhere.
3. **e2e IT isolation.** `runOnce()` sweeps ALL `Buyer_Acceptance` candidates
   (15 pre-exist in the shared dev DB). The IT is `@Transactional` so every
   write (incl. any effect on those 15) rolls back; assertions target the seeded
   offer's `source_id`. Because the job is synchronous, `sendTemplated`'s
   `@Transactional` joins the test tx — an `entityManager.flush()` after
   `runOnce()` surfaces the in-session SENT-status + flag writes to the raw-JDBC
   assertions before rollback (without it the raw read saw the un-flushed
   PENDING insert).
4. **Email copy is best-effort** — ops must review the V105 bodies before
   flipping `pws.counter-reminder.enabled=true`. The templates ship `enabled=true`
   (so `sendTemplated` resolves them), but the job-level flag defaults `false`,
   so nothing sends until deliberately enabled.
5. **No PWS `sales_email` / CC.** User-locked buyer-only recipients — the
   `pws_constants.sales_email` column is intentionally not used here.
