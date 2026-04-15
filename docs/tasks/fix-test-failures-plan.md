# Plan: Fix Backend Test Failures (Post H-1 / H-3)

_Generated 2026-04-13. Covers 37 NPE errors + 2 assertion failures in `mvn test`._

## Root causes

### Cause 1 — `@InjectMocks` misses `OfferItemDeviceLoader` (37 errors)

H-3 extracted `OfferItemDeviceLoader` as a new Spring `@Component` and
injected it into `OfferService.java:88` and `OfferReviewService.java`. The
existing unit tests use `@InjectMocks` for both services but do **not**
declare a `@Mock OfferItemDeviceLoader`, so Mockito sets the field to
`null`. Every test path that reaches `loadDeviceMap(...)` then throws
`NullPointerException: Cannot invoke "OfferItemDeviceLoader.loadDeviceMap(...)"
because "this.deviceLoader" is null`.

Affected tests (all NPE, same cause):
- `OfferServiceTest` — 26 errors across `GetOrCreateCart`, `UpsertCartItem`,
  `RemoveItem`, `ResetCart`, `SubmitCart`, `SubmitOffer`, `SubmitOrder`,
  `ExportCartCsv`, `UpsertCartItemCaseLot`, `SubmitCartCaseLot`,
  `SubmitOfferEdgeCases`.
- `OfferReviewServiceTest` — 10 errors across `SetItemAction`,
  `UpdateItemCounter`, `GetOfferDetail`, `BulkActions`, `CompleteReview`.
- Note: `CounterOfferServiceTest` is NOT affected by this — it does not
  use `OfferItemDeviceLoader`.

### Cause 2 — `OfferReviewService.setItemAction` calls `loadDeviceMap` before validating action (1 assertion failure, surfaced by Cause 1)

`OfferReviewServiceTest.SetItemAction.setItemAction_invalidAction_throws`
expects `IllegalArgumentException` for an invalid action. The service
(`OfferReviewService.java:208`) calls `loadDeviceMap(List.of(item))`
**before** the `switch` that validates the action. With Cause 1 fixed
(deviceLoader mocked), this test will pass. Without the mock, it throws
NPE instead of IAE — the "failure" is secondary to the same cause.

Order-of-operations is also defensible on its own (don't do work before
validating input), but that's a Low-priority cleanup, not required for
green tests.

### Cause 3 — Real bug: `CounterOfferService.cancelOffer` sets `STATUS_DECLINED` (1 assertion failure)

`CounterOfferService.java:257`:
```java
offer.setStatus(STATUS_DECLINED);   // ← bug
offer.setCanceledOn(LocalDateTime.now());
```

The method is named `cancelOffer`, docstring says
"Clones ACT_Offer_BuyerCancelOffer", and it sets `canceledOn`. It should
set `STATUS_CANCELED`. Test
`CounterOfferServiceTest.CancelOffer.cancelOffer_setsCanceledStatus`
correctly asserts `"Canceled"`. This is a genuine business-logic bug
introduced (or overlooked) during the H-1 constants extraction — someone
mapped `"Declined"` to the wrong named constant. Low blast radius: the
offer still lands in a terminal state, but it collapses the UI
distinction between a buyer-decline and a buyer-cancel.

Mendix parity: `migration_context/backend/ACT_Offer_BuyerCancelOffer.md`
sets `SalesOfferStatus = Canceled`.

## Fix plan

Three surgical commits, each independently testable.

### Fix 1 — Declare `OfferItemDeviceLoader` mock in affected tests

**Files:**
- `backend/src/test/java/com/ecoatm/salesplatform/service/OfferServiceTest.java`
- `backend/src/test/java/com/ecoatm/salesplatform/service/OfferReviewServiceTest.java`

**Change:** Add to each class's `@Mock` block:
```java
@Mock
private OfferItemDeviceLoader deviceLoader;
```
(Mockito `@InjectMocks` will wire it into the service by type.)

**Stubbing:** Add a lenient default in each `@BeforeEach`:
```java
when(deviceLoader.loadDeviceMap(anyList())).thenReturn(Map.of());
```
This matches the pre-H-3 behavior where the in-service `loadDeviceMap`
hit the mocked `DeviceRepository` and got an empty list back. Tests that
need a populated map can override with a local `when(...)` stub.

**Audit:** Grep each test file for `deviceRepository.findAllById` /
`when(deviceRepository...)` — if any test stubbed the old device
repository path, replace with a `deviceLoader.loadDeviceMap` stub
returning the equivalent `Map<Long, Device>`.

**Expected result:** 37 errors → 0. `setItemAction_invalidAction_throws`
also goes green (Cause 2 resolved transitively).

### Fix 2 — `CounterOfferService.cancelOffer` uses `STATUS_CANCELED`

**File:** `backend/src/main/java/com/ecoatm/salesplatform/service/CounterOfferService.java:257`

**Change:**
```java
-        offer.setStatus(STATUS_DECLINED);
+        offer.setStatus(STATUS_CANCELED);
```

`STATUS_CANCELED` is already imported at line 36. No other edits.

**Expected result:** `cancelOffer_setsCanceledStatus` goes green. One
caveat to verify: check whether any downstream query or UI filter treats
`"Declined"` as the terminal state for canceled offers — if so, it was
relying on the bug and needs its own follow-up. Grep:
```
rg '"Canceled"|STATUS_CANCELED|getCanceledOn' backend/src/main
rg '"Declined"|STATUS_DECLINED' backend/src/main
```

### Fix 3 — (Optional, Low) Validate action before loading devices

**File:** `OfferReviewService.java:200-240`

Move the `switch` above the `loadDeviceMap` call, or extract a small
`validateAction(action)` guard that runs first. Not required for green
tests once Fix 1 lands; listed here so the sequencing smell isn't
forgotten. Ship separately if at all.

## Verification

```bash
cd backend
mvn -q test -Dtest='OfferServiceTest,OfferReviewServiceTest,CounterOfferServiceTest'
```

Expected: all three test classes green, no NPEs, no assertion failures.
Then run the full suite:

```bash
mvn -q test
```

Target: 444 tests run, 0 failures, 0 errors (matches pre-H-3 baseline).

## Risk

- **Fix 1:** Zero production risk (test-only).
- **Fix 2:** Changes a persisted status string on cancel. If any Flyway
  seed data, report, or integration relies on `canceledOn IS NOT NULL
  AND status = 'Declined'`, it needs updating — but the codebase should
  be using `canceledOn` as the canonical signal. One-line grep audit
  covers it.
- **Fix 3:** Pure reordering of validation; no functional change.

## References

- Plan entry: `docs/tasks/simplification-analysis.md` (H-1, H-3 DONE)
- Mendix source: `migration_context/backend/ACT_Offer_BuyerCancelOffer.md`
- Constants: `backend/src/main/java/com/ecoatm/salesplatform/model/pws/PwsOfferStatus.java`
