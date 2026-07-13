# Task 2.3.E — Bulk Offer-Status metadata-fix report

**STATUS:** DONE

**Branch:** `worktree-agent-aeb38bbbfb3d61eae` (cut from `main`; fast-forwarded
to `e57820c6` — the "Merge 2.3 Chunk E" commit that carries the bulk-status tool
— because the worktree was originally cut from a stale pre-merge `main`).

**Commits:**
- `07d76a40` — `fix(pws): reject metadata-only bulk offer-status change; drop pretend-success path` (code + tests)
- `179be623` — `docs(pws): bulk offer-status metadata-only path now 400 (review fix)` (docs)

## The defect (review finding)

The metadata-only path (`notOrderStatusChange=true`) was a **pretend-success**.
`pws.order` has no `has_shipment_details` / `legacy_order` column and this feature
ships no migration, so `applyMetadata` could not persist the requested
`hasShipmentDetails` flag — yet it bumped `updated_date` on the resolved orders
and returned `200 metadataOnly:true`, giving the operator no signal the flag write
was a no-op. This is exactly the silent-broken-path the ship-and-iterate principle
forbids.

## The fix (surgical)

1. **Reject at the earliest point.** `ChangeOfferStatusValidator.validate` now
   throws `IllegalArgumentException` (→ 400 via the existing
   `GlobalExceptionHandler`) on `notOrderStatusChange=true`, message:
   `"Metadata-only bulk update (notOrderStatusChange) is not supported until
   pws.order has a has_shipment_details column"`. The guard is the first statement
   in `validate`, which is itself the first statement in
   `BulkOfferStatusService.changeStatus` — so nothing is resolved, touched, or
   audited before the rejection. The now-always-true `statusChange` branch was
   unwrapped.
2. **Removed the pretend-success path.** `applyMetadata` (the `updated_date` bump,
   the flag-in-audit, and the `metadataOnly:true` return) is deleted from the
   service; the `changeStatus` ternary now always calls `applyStatusChange`; the
   dead `metadataOnly` ternaries in `writeAudit` were collapsed to their
   status-change values (the emitted status-change audit row is byte-identical);
   the unused `Collectors` import was removed.
3. **Kept the status-change path exactly as-is** — from-status guard,
   allPeriod-vs-filtered, single `@Transactional`, one audit row, Administrator-only
   authz. No migration added (deliberate future schema-prep chunk).

## Tests (TDD — RED then GREEN)

- **RED (proof):** modified metadata-only cases failed on the pre-fix code
  ("Expecting code to raise a throwable") — `ChangeOfferStatusValidatorTest`
  (2 cases), `BulkOfferStatusServiceTest` (1 case).
- **GREEN:** `./mvnw test -Dtest=ChangeOfferStatusValidatorTest,BulkOfferStatusServiceTest,BulkOfferStatusControllerIT -Dspring.profiles.active=pg-test`
  → **Tests run: 24, Failures: 0, Errors: 0 — BUILD SUCCESS**
  (`ChangeOfferStatusValidatorTest` 12 + `BulkOfferStatusServiceTest` 4 +
  `BulkOfferStatusControllerIT` 8; the controller IT gained one metadata-only
  400 case, proving the rejection end-to-end through the controller +
  `GlobalExceptionHandler`).

## Concerns / notes

- **Worktree base mismatch:** the worktree HEAD started at `17827f21` (a 2.4 line)
  with **zero** unique commits and no bulk-status tool. It is a strict ancestor of
  `main`, so I fast-forwarded to `main` (`e57820c6`, clean — no conflicts possible)
  to get the tool the task targets. Flagging in case the orchestrator expected the
  worktree to already contain the tool.
- **`hasShipmentDetails` DTO field retained.** It is still on
  `ChangeOfferStatusRequest` and still logged in the status-change audit `reason`
  (always `false` now). Left in place — removing it is out of scope and would churn
  the DTO/JSON contract; it is simply inert until the deferred schema-prep chunk.
- **`ChangeOfferStatusResult.metadataOnly` retained** (always `false`) — the
  status-change response contract and the controller-IT `metadataOnly:false`
  assertion depend on it.
- Docs updated to match: `docs/api/rest-endpoints.md`,
  `docs/app-metadata/modules.md`, `docs/testing/coverage.md`.
