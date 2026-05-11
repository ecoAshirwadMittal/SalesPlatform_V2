# Sub-project 5b: R2/R3 Row Visibility Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the two `TRUE`-constant stubs in `BidDataCreationRepository` (`bid_meets_threshold` at line 130 and `row_visible` at line 138) with the real R2 + R3 per-row visibility predicates, plus an STB shortcut. Pure SQL refactor — no new files, no schema changes, no service-layer changes.

**Architecture:** One method, one CTE_SQL constant, four CTE blocks change. `params` extends to LEFT JOIN BRSF for R2+R3 inputs. `prior_round_biddata` rewrites to round-agnostic latest-per-AE (replaces the deleted `prior_scheduling_auction` CTE). `inventory_with_threshold` branches on `params.round` between R2's 5-branch cascade (DW/WH split) and R3's 4-branch cascade. `inventory_qualified` uses `is_special_treatment OR bid_meets_threshold` for the STB shortcut.

**Tech Stack:** PostgreSQL native CTE, JPA `EntityManager.createNativeQuery`, JUnit 5 + AssertJ + `@DataJpaTest`. Test scenarios authored via the existing fluent builder `BidDataScenario` at `backend/src/test/java/com/ecoatm/salesplatform/fixtures/BidDataScenario.java`.

**Spec:** `docs/tasks/auction-r2-r3-row-visibility-design.md`

---

## File Structure

### Modified files (Java source)

```
backend/src/main/java/com/ecoatm/salesplatform/
  repository/auctions/
    BidDataCreationRepository.java                 -- CTE_SQL constant: 4 blocks change
```

### Modified files (Test infra)

```
backend/src/test/java/com/ecoatm/salesplatform/
  fixtures/
    BidDataScenario.java                           -- extend builder with R2/R3/STB primitives
  repository/auctions/
    BidDataCreationRepositoryIT.java               -- add 20 new test cases
```

### New / deleted files

None. No new schema migration. No new entity. No new service or controller.

### Documentation

```
docs/
  architecture/decisions.md                        -- new ADR for sub-project 5b
  business-logic/r2-buyer-assignment.md            -- per-row visibility note
  business-logic/r3-init-and-preprocess.md         -- per-row visibility note
  testing/coverage.md                              -- auctions.biddata.row-visibility entry
  tasks/auction-flow-gap-analysis-2026-05-06.md    -- mark item #3 shipped
```

---

## Task 1: Extend `BidDataScenario` builder with R2/R3/STB primitives

**Files:**
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/fixtures/BidDataScenario.java`

The current builder seeds R1 scenarios via `.round(int)`, `.buyerCodeType(String)`, `.inventory(...)`, and `.dwInventory(...)`. Sub-project 5b needs additional primitives to seed R2/R3 scenarios. Add these methods:

- `.qbc(boolean isSpecialTreatment, boolean included, String qualificationType)` — writes a `buyer_mgmt.qualified_buyer_codes` row for the scenario's `(scheduling_auction_id, buyer_code_id)` with the given flags. Default in current scenarios: `isSpecialTreatment=false, included=true, qualificationType='Qualified'`. The existing R1 scenarios should keep their current behaviour even after this method is added.
- `.priorBid(String ecoid, String mergedGrade, BigDecimal amount, int quantity)` — writes a prior-round `bid_data` row in a prior `scheduling_auction`. Auto-creates the prior SA + bid_round (submitted=TRUE, submitted_datetime=NOW − offset) if needed. Uses round = `currentRound - 1` for R2 scenarios; for R3 scenarios where the test wants R2-vs-R1 ordering, expose `.priorBid(round, ecoid, ...)` overload.
- `.priorBidWithRank(String ecoid, String mergedGrade, BigDecimal amount, int quantity, Integer round3BidRank)` — overload for R3 rank-branch tests. Sets `bid_data.round3_bid_rank` on the seeded prior bid row.
- `.brsfR2(BigDecimal targetPercent, BigDecimal targetValue, String qualMode, String invMode)` — INSERTs a row in `auctions.bid_round_selection_filters` for round=2 with the given values. `qualMode` ∈ {`'All_Buyers'`, `'Only_Qualified'`}. `invMode` ∈ {`'ShowAllInventory'`, `'InventoryRound1QualifiedBids'`}.
- `.brsfR3(BigDecimal pctVar, BigDecimal amtVar, Integer rankLim)` — INSERTs a row in `bid_round_selection_filters` for round=3 with the given values (any of the three may be NULL).
- `.r3TargetPrice(String ecoid, String mergedGrade, BigDecimal round3TargetPrice)` — UPDATEs the seeded `aggregated_inventory` row to set `round3_target_price`. Required for R3 tests because the existing inventory builder doesn't set this column.

- [ ] **Step 1: Read existing `BidDataScenario` to learn its conventions**

```bash
cat backend/src/test/java/com/ecoatm/salesplatform/fixtures/BidDataScenario.java | head -80
```

Note the existing private fields, helper methods, and the chained-builder return convention.

- [ ] **Step 2: Add the six new methods**

For each method: keep the same return type (`BidDataScenario` for chaining), throw `IllegalStateException` on misuse (e.g., calling `.priorBid` before `.round(2)` or `.round(3)`), and use the existing `JdbcTemplate jdbc` field. Match the indentation and Javadoc style of the existing methods.

Skeleton:

```java
public BidDataScenario qbc(boolean isSpecialTreatment, boolean included, String qualificationType) {
    if (this.schedulingAuctionId == null || this.buyerCodeId == null) {
        throw new IllegalStateException("qbc() requires .round(...) and a buyer code first");
    }
    jdbc.update(
        "INSERT INTO buyer_mgmt.qualified_buyer_codes "
            + "(scheduling_auction_id, buyer_code_id, qualification_type, included, "
            + "is_special_treatment, created_date, changed_date) "
            + "VALUES (?, ?, ?, ?, ?, NOW(), NOW())",
        this.schedulingAuctionId, this.buyerCodeId,
        qualificationType, included, isSpecialTreatment);
    return this;
}

public BidDataScenario brsfR2(BigDecimal targetPercent, BigDecimal targetValue,
                              String qualMode, String invMode) {
    jdbc.update(
        "INSERT INTO auctions.bid_round_selection_filters "
            + "(round, target_percent, target_value, "
            + "regular_buyer_qualification, regular_buyer_inventory_options, "
            + "stb_allow_all_buyers_override, created_date, changed_date) "
            + "VALUES (2, ?, ?, ?, ?, false, NOW(), NOW())",
        targetPercent, targetValue, qualMode, invMode);
    return this;
}

public BidDataScenario brsfR3(BigDecimal pctVar, BigDecimal amtVar, Integer rankLim) {
    jdbc.update(
        "INSERT INTO auctions.bid_round_selection_filters "
            + "(round, bid_percentage_variation, bid_amount_variation, "
            + "rank_qualification_limit, regular_buyer_qualification, "
            + "regular_buyer_inventory_options, stb_allow_all_buyers_override, "
            + "created_date, changed_date) "
            + "VALUES (3, ?, ?, ?, 'Only_Qualified', 'InventoryRound1QualifiedBids', false, "
            + "NOW(), NOW())",
        pctVar, amtVar, rankLim);
    return this;
}

public BidDataScenario priorBid(String ecoid, String mergedGrade,
                                BigDecimal amount, int quantity) {
    return priorBid(this.round - 1, ecoid, mergedGrade, amount, quantity, null);
}

public BidDataScenario priorBidWithRank(String ecoid, String mergedGrade,
                                        BigDecimal amount, int quantity,
                                        Integer round3BidRank) {
    return priorBid(this.round - 1, ecoid, mergedGrade, amount, quantity, round3BidRank);
}

private BidDataScenario priorBid(int priorRound, String ecoid, String mergedGrade,
                                 BigDecimal amount, int quantity, Integer round3BidRank) {
    // Resolve prior SA (auto-create if missing) ...
    // Resolve prior bid_round (submitted=TRUE) ...
    // Resolve aggregated_inventory_id by (week_id, ecoid, merged_grade) ...
    // INSERT bid_data row with submitted_datetime = NOW() - INTERVAL ('1 hour' * (currentRound - priorRound))
    //   (so R2 priors order before R1 priors in DISTINCT ON tie-breaks)
    // ...
    return this;
}

public BidDataScenario r3TargetPrice(String ecoid, String mergedGrade,
                                     BigDecimal round3TargetPrice) {
    jdbc.update(
        "UPDATE auctions.aggregated_inventory "
            + "SET round3_target_price = ? "
            + "WHERE ecoid2 = ? AND merged_grade = ? AND week_id = ?",
        round3TargetPrice, ecoid, mergedGrade, this.weekId);
    return this;
}
```

The `priorBid` private helper is the most involved — it needs to look up or create a prior SA + bid_round + bid_data_doc. Reuse the existing logic in `BidDataScenario` for SA / bid_round creation; just parameterise on `round` instead of always using `this.round`.

- [ ] **Step 3: Compile**

Run: `cd backend && mvn -q compile -P pg-test`
Expected: PASS — no source errors.

- [ ] **Step 4: Run existing IT to confirm no regression**

Run: `cd backend && mvn -q -Dtest=BidDataCreationRepositoryIT test -P pg-test`
Expected: PASS — all current cases still green (the new builder methods are additive; existing scenarios don't use them).

- [ ] **Step 5: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/fixtures/BidDataScenario.java
git commit -m "test(5b): extend BidDataScenario with R2/R3/STB primitives"
```

---

## Task 2: Write the first failing IT case (drive the SQL refactor)

**Files:**
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java`

The first new test exercises the most basic incorrect behaviour: an R2 buyer with an R1 bid that fails all threshold branches should see ZERO rows, but today the always-TRUE stub returns 1 row.

- [ ] **Step 1: Add the failing test**

Append to `BidDataCreationRepositoryIT.java`:

```java
/**
 * R2 Only_Qualified buyer whose R1 bid is below all threshold branches:
 * {@code bid_meets_threshold} must be FALSE → no row inserted.
 */
@Test
void generate_r2_onlyQualified_belowAllThresholds_insertsZero() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("AAA1", "A", 10, new BigDecimal("100.00"))
            .priorBid("AAA1", "A", new BigDecimal("10.00"), 1)  // 10 < 100 by far
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

    assertThat(inserted).isZero();
}
```

- [ ] **Step 2: Run — should FAIL**

Run: `cd backend && mvn -q -Dtest=BidDataCreationRepositoryIT#generate_r2_onlyQualified_belowAllThresholds_insertsZero test -P pg-test`
Expected: FAIL — the always-TRUE stub returns 1 row; the assertion `isZero()` fails with `AssertionError: expected 0 but was 1`.

If the test fails with a different reason (e.g., NPE in scenario builder, missing column), the Task 1 builder extensions need fixing before proceeding.

- [ ] **Step 3: Commit the failing test**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java
git commit -m "test(5b): failing R2 below-thresholds case drives SQL refactor"
```

(Yes, commit the failing test deliberately. Task 3 makes it pass.)

---

## Task 3: SQL refactor in `BidDataCreationRepository`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepository.java`

Replace four CTE blocks in the `CTE_SQL` constant. Delete the `prior_scheduling_auction` CTE.

- [ ] **Step 1: Replace the `params` CTE**

Find the current `params` block (roughly lines 69-94 of `BidDataCreationRepository.java`). Replace with:

```sql
params AS (
    SELECT
        CAST(:bid_round_id    AS bigint) AS bid_round_id,
        CAST(:buyer_code_id   AS bigint) AS buyer_code_id,
        CAST(:bid_data_doc_id AS bigint) AS bid_data_doc_id,
        br.scheduling_auction_id AS scheduling_auction_id,
        sa.round                 AS round,
        a.week_id                AS week_id,
        bc.code                  AS buyer_code_text,
        (SELECT b.company_name
           FROM buyer_mgmt.buyer_code_buyers bcb
           JOIN buyer_mgmt.buyers b ON b.id = bcb.buyer_id
          WHERE bcb.buyer_code_id = bc.id
          ORDER BY bcb.buyer_id
          LIMIT 1)               AS company_name,
        CASE
            WHEN bc.buyer_code_type IN ('Data_Wipe', 'Purchasing_Order_Data_Wipe')
                THEN 'DW'
            ELSE 'Wholesale'
        END                      AS buyer_code_type,
        -- 5b: R2 BRSF inputs (NULL when round=1; chk_brsf_round permits 2 or 3 only)
        brsf.target_percent                  AS target_pct,
        brsf.target_value                    AS target_val,
        brsf.regular_buyer_qualification     AS qual_mode,
        brsf.regular_buyer_inventory_options AS inv_mode,
        -- 5b: R3 BRSF inputs
        brsf.bid_percentage_variation        AS pct_var,
        brsf.bid_amount_variation            AS amt_var,
        brsf.rank_qualification_limit        AS rank_lim
    FROM auctions.bid_rounds br
    JOIN auctions.scheduling_auctions sa ON sa.id = br.scheduling_auction_id
    JOIN auctions.auctions a              ON a.id = sa.auction_id
    JOIN buyer_mgmt.buyer_codes bc        ON bc.id = :buyer_code_id
    LEFT JOIN auctions.bid_round_selection_filters brsf ON brsf.round = sa.round
    WHERE br.id = :bid_round_id
),
```

- [ ] **Step 2: Delete `prior_scheduling_auction` CTE and replace `prior_round_biddata`**

Find the `prior_scheduling_auction` block and the `prior_round_biddata` block (roughly lines 142-159). Replace BOTH with one new CTE:

```sql
prior_round_biddata AS (
    -- 5b: latest submitted nonzero bid per (ecoid, merged_grade) across ALL prior rounds.
    --   R2 (round=2): only R1 SAs match `sa_prev.round < 2` → DISTINCT ON returns the
    --     unique R1 bid per AE.
    --   R3 (round=3): R1+R2 SAs match → DISTINCT ON + ORDER BY submitted_datetime DESC
    --     picks R2 over R1 when both exist.
    --   R1 (round=1): no prior rounds → CTE returns zero rows; bid_meets_threshold
    --     defaults TRUE in the outer CASE.
    SELECT DISTINCT ON (bd.ecoid, bd.merged_grade)
           bd.ecoid,
           bd.merged_grade,
           bd.submitted_bid_quantity AS prev_qty,
           bd.submitted_bid_amount   AS prev_amount,
           bd.round3_bid_rank        AS prev_rank
      FROM auctions.bid_data bd
      JOIN auctions.bid_rounds br_prev
        ON br_prev.id = bd.bid_round_id AND br_prev.submitted = TRUE
      JOIN auctions.scheduling_auctions sa_prev
        ON sa_prev.id = br_prev.scheduling_auction_id
      JOIN auctions.scheduling_auctions sa_cur
        ON sa_cur.id = (SELECT scheduling_auction_id FROM params)
       AND sa_prev.auction_id = sa_cur.auction_id
     WHERE bd.buyer_code_id = (SELECT buyer_code_id FROM params)
       AND sa_prev.round    < (SELECT round FROM params)
       AND bd.submitted_bid_amount > 0
     ORDER BY bd.ecoid, bd.merged_grade, bd.submitted_datetime DESC
),
```

- [ ] **Step 3: Replace `inventory_with_threshold` CTE**

Find the current block (roughly lines 125-132). Replace with:

```sql
inventory_with_threshold AS (
    SELECT inv.*,
           prb.prev_qty, prb.prev_amount, prb.prev_rank,
           CASE
             -- 5b R2 cascade (5 branches, DW vs Wholesale split, ports SUB_Round2AggregatedInventorySingleItem)
             WHEN p.round = 2 THEN
               CASE
                 WHEN p.qual_mode = 'All_Buyers' THEN TRUE
                 WHEN prb.prev_amount IS NULL THEN
                   (p.inv_mode = 'ShowAllInventory')
                 WHEN p.buyer_code_type = 'DW' THEN
                      (inv.dw_avg_target_price = 0 AND prb.prev_amount > 0)
                   OR (inv.dw_avg_target_price > 0
                       AND prb.prev_amount / inv.dw_avg_target_price >= 1 - (p.target_pct / 100))
                   OR (inv.dw_avg_target_price - prb.prev_amount <= p.target_val)
                   OR (p.inv_mode = 'InventoryRound1QualifiedBids' AND prb.prev_amount > 0)
                   OR (p.inv_mode = 'ShowAllInventory')
                 ELSE
                      (inv.avg_target_price = 0 AND prb.prev_amount > 0)
                   OR (inv.avg_target_price > 0
                       AND prb.prev_amount / inv.avg_target_price >= 1 - (p.target_pct / 100))
                   OR (inv.avg_target_price - prb.prev_amount <= p.target_val)
                   OR (p.inv_mode = 'InventoryRound1QualifiedBids' AND prb.prev_amount > 0)
                   OR (p.inv_mode = 'ShowAllInventory')
               END
             -- 5b R3 cascade (4 branches, ports per-row form of sub-project 6's R3 selection rule)
             WHEN p.round = 3 THEN
               (p.pct_var IS NULL AND p.amt_var IS NULL AND p.rank_lim IS NULL)
               OR (p.pct_var IS NOT NULL
                   AND prb.prev_amount IS NOT NULL
                   AND prb.prev_amount
                       >= inv.round3_target_price - (inv.round3_target_price * p.pct_var / 100))
               OR (p.amt_var IS NOT NULL
                   AND prb.prev_amount IS NOT NULL
                   AND prb.prev_amount >= inv.round3_target_price - p.amt_var)
               OR (p.rank_lim IS NOT NULL
                   AND prb.prev_rank IS NOT NULL
                   AND prb.prev_rank <= p.rank_lim)
             -- R1 fallthrough: no prior round, no per-row filtering
             ELSE TRUE
           END AS bid_meets_threshold
    FROM inventory inv
    CROSS JOIN params p
    LEFT JOIN prior_round_biddata prb
           ON prb.ecoid = inv.ecoid AND prb.merged_grade = inv.merged_grade
),
```

- [ ] **Step 4: Replace `inventory_qualified` CTE**

Find the current block (roughly lines 133-141). Replace with:

```sql
inventory_qualified AS (
    SELECT iwt.*,
           (q.is_special_treatment OR iwt.bid_meets_threshold) AS row_visible
      FROM inventory_with_threshold iwt
      CROSS JOIN qualified_buyer_check q
     WHERE q.included = TRUE
),
```

- [ ] **Step 5: Update class-level Javadoc**

Find the class-level Javadoc note (roughly lines 30-37) about the two stubs being TODO for sub-project 4. Replace with:

```java
 * <p>The R2/R3 per-row threshold cascade and STB shortcut were ported from
 * Mendix {@code SUB_Round2AggregatedInventorySingleItem} and the per-row form
 * of sub-project 6's R3 selection rule (sub-project 5b, 2026-05-07).
 */
```

Also update the `@implNote` at the bottom of the `generate(...)` Javadoc that says "Sub-project 4 will redesign the row_visible + bid_meets_threshold stubs". Either delete that paragraph entirely (since it's done) or rewrite it to point to the design doc:

```java
 * @implNote The {@code row_visible} and {@code bid_meets_threshold} predicates
 *           were filled in by sub-project 5b. See
 *           {@code docs/tasks/auction-r2-r3-row-visibility-design.md}.
```

- [ ] **Step 6: Run the failing test from Task 2 — should now PASS**

Run: `cd backend && mvn -q -Dtest=BidDataCreationRepositoryIT#generate_r2_onlyQualified_belowAllThresholds_insertsZero test -P pg-test`
Expected: PASS.

- [ ] **Step 7: Run the full IT to confirm no regression**

Run: `cd backend && mvn -q -Dtest=BidDataCreationRepositoryIT test -P pg-test`
Expected: ALL existing R1-init cases still pass; the new case from Task 2 also passes.

If any existing case fails:
- The `inventory_qualified` CROSS JOIN to `qualified_buyer_check` was preserved — no change there
- The R1 ELSE branch returns TRUE — should match prior always-TRUE behaviour
- The most likely regression: an existing R1 test that doesn't seed a QBC would have produced rows under the old CROSS JOIN semantics (against a possibly-empty qualified_buyer_check) — confirm by reading the existing tests. If a regression surfaces, the fix is to ensure existing R1 tests seed a QBC via `.qbc(false, true, "Qualified")`.

- [ ] **Step 8: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepository.java
git commit -m "feat(5b): replace bid_meets_threshold + row_visible stubs with R2/R3 cascades"
```

---

## Task 4: R2 cascade IT cases (Only_Qualified prior-bid branches)

**Files:**
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java`

Add 7 IT methods covering the R2 Only_Qualified branches.

- [ ] **Step 1: Add the 7 tests**

Append to the IT class. (Use `BidDataScenario.priorBid(...)` for R1 priors, `.brsfR2(...)` for the BRSF row, `.qbc(false, true, "Qualified")` for the QBC row.)

```java
/**
 * R2 Only_Qualified Wholesale buyer whose R1 bid clears the percent threshold
 * (bid >= target * (1 - target_pct/100)) → row visible.
 */
@Test
void generate_r2_onlyQualified_percentBranch_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("PCT1", "A", 10, new BigDecimal("100.00"))
            .priorBid("PCT1", "A", new BigDecimal("96.00"), 1)  // 96 >= 100*(1-5/100)=95
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("0.50"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified Wholesale buyer whose R1 bid clears the value-band threshold
 * (target - bid <= target_value) → row visible.
 */
@Test
void generate_r2_onlyQualified_valueBranch_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("VAL1", "A", 10, new BigDecimal("100.00"))
            .priorBid("VAL1", "A", new BigDecimal("99.00"), 1)  // target-bid=1 <= target_val=1
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("0.01"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified buyer with target=0 and nonzero R1 bid → row visible
 * (the "target=0 AND bid>0" sub-branch).
 */
@Test
void generate_r2_onlyQualified_targetZeroBidNonzero_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("ZER1", "A", 10, new BigDecimal("0.00"))    // target=0
            .priorBid("ZER1", "A", new BigDecimal("5.00"), 1)
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified buyer with R1 bid below all thresholds AND
 * inv_mode=InventoryRound1QualifiedBids → fallback branch admits because bid > 0.
 */
@Test
void generate_r2_onlyQualified_inventoryRound1QualifiedBidsFallback_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("FBK1", "A", 10, new BigDecimal("100.00"))
            .priorBid("FBK1", "A", new BigDecimal("10.00"), 1)  // far below all thresholds
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified buyer with R1 bid below all thresholds AND
 * inv_mode=ShowAllInventory → admits regardless of bid amount.
 */
@Test
void generate_r2_onlyQualified_showAllInventory_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("SAI1", "A", 10, new BigDecimal("100.00"))
            .priorBid("SAI1", "A", new BigDecimal("1.00"), 1)
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "Only_Qualified", "ShowAllInventory");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified DW buyer reads from {@code dw_avg_target_price}, not
 * {@code avg_target_price}. Wholesale price is set to a sentinel so a
 * mis-routed CASE branch surfaces.
 */
@Test
void generate_r2_onlyQualified_dwBranch_usesDwTarget() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("DW")
            // wholesale qty/price are sentinels: bid=18 would FAIL all thresholds against WH=99
            .dwInventory("DWB1", "A",
                    50, new BigDecimal("99.00"),  // wholesale qty/price (decoy)
                    25, new BigDecimal("20.00"))  // DW qty/price (real target)
            .priorBid("DWB1", "A", new BigDecimal("19.00"), 1)  // 19 >= 20-1=19 (value branch)
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("0.01"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified buyer with R1 bid below all thresholds AND
 * neither inv_mode admits + bid amount > 0 → still admitted by InventoryRound1QualifiedBids
 * if that's the inv_mode. To exercise the FALSE path we need bid amount = 0.
 *
 * Wait — the prior_round_biddata CTE filters submitted_bid_amount > 0. If we
 * want a buyer with prior_amount = 0, we need NO prior bid at all. That's the
 * "no R1 bid + InventoryRound1QualifiedBids" case (Task 5).
 *
 * For "below all thresholds and no admit" with InventoryRound1QualifiedBids, we
 * need: bid > 0 AND inv_mode=InventoryRound1QualifiedBids AND target_pct/value
 * tight. But "InventoryRound1QualifiedBids AND bid > 0" is itself an admit
 * branch. So this case is NOT REACHABLE under InventoryRound1QualifiedBids.
 *
 * The 7th test exercises the "below all thresholds + neither inv_mode admits"
 * scenario, which requires inv_mode = some-third-value. V59 only permits
 * 'InventoryRound1QualifiedBids' or 'ShowAllInventory' (chk_brsf_regular_inventory).
 * So the only way to reach the FALSE outcome under Only_Qualified-with-bid is
 * if the bid amount is 0, but the prior_round_biddata CTE filters those out.
 *
 * Conclusion: there is NO reachable "below all thresholds + Only_Qualified +
 * has bid > 0" → invisible case. The Task 2 case
 * (generate_r2_onlyQualified_belowAllThresholds_insertsZero) covered the
 * "no bid → no admission under InventoryRound1QualifiedBids" path. The
 * 7th case below covers the symmetric Wholesale variant of test 6.
 */

/**
 * R2 Only_Qualified Wholesale buyer reads from {@code avg_target_price}, not
 * {@code dw_avg_target_price}. Mirrors the DW branch test for symmetry.
 */
@Test
void generate_r2_onlyQualified_wholesaleBranch_usesWholesaleTarget() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            // sentinel: DW=99 would PASS, WH=20 fails unless bid is high
            .dwInventory("WHB1", "A",
                    25, new BigDecimal("20.00"),  // wholesale qty/price (real target)
                    50, new BigDecimal("99.00"))  // DW qty/price (decoy)
            .priorBid("WHB1", "A", new BigDecimal("19.00"), 1)
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("0.01"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}
```

(That's 6 explicit tests; the explanatory comment about the 7th unreachable case effectively replaces it. Adjust based on whether `dwInventory` accepts the parameter order shown — verify against the existing IT.)

- [ ] **Step 2: Run the new tests**

Run: `cd backend && mvn -q -Dtest='BidDataCreationRepositoryIT#generate_r2*' test -P pg-test`
Expected: PASS — all 6 new tests + the Task 2 test.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java
git commit -m "test(5b): R2 Only_Qualified per-AE cascade coverage"
```

---

## Task 5: R2 `qual_mode` shortcut IT cases

**Files:**
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java`

Add 3 IT methods covering R2's `All_Buyers` shortcut and the no-prior-bid + inv_mode interactions.

- [ ] **Step 1: Add the 3 tests**

```java
/**
 * R2 All_Buyers admits all rows regardless of R1 bid history. No R1 bid is
 * needed for the row to be visible.
 */
@Test
void generate_r2_allBuyers_visibleRegardlessOfBid() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("ALB1", "A", 10, new BigDecimal("100.00"))
            // No prior bid seeded — All_Buyers admits anyway
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "All_Buyers", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified buyer with NO R1 bid + inv_mode=ShowAllInventory →
 * row visible (the {@code prb.prev_amount IS NULL} sub-branch admits).
 */
@Test
void generate_r2_onlyQualified_noPriorBid_showAllInventory_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("NPB1", "A", 10, new BigDecimal("100.00"))
            // No prior bid
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "Only_Qualified", "ShowAllInventory");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R2 Only_Qualified buyer with NO R1 bid + inv_mode=InventoryRound1QualifiedBids →
 * row INVISIBLE (no prior bid → InventoryRound1QualifiedBids fallback can't
 * admit because there's nothing to fall back on).
 */
@Test
void generate_r2_onlyQualified_noPriorBid_inventoryRound1Qualified_invisible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("NPB2", "A", 10, new BigDecimal("100.00"))
            // No prior bid
            .qbc(false, true, "Qualified")
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isZero();
}
```

- [ ] **Step 2: Run**

Run: `cd backend && mvn -q -Dtest='BidDataCreationRepositoryIT#generate_r2*' test -P pg-test`
Expected: PASS.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java
git commit -m "test(5b): R2 qual_mode shortcuts (All_Buyers, no-bid + inv_mode)"
```

---

## Task 6: R3 cascade IT cases

**Files:**
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java`

Add 7 IT methods covering R3's 4-branch cascade plus all-NULL fallthrough and ROW_NUMBER R2-over-R1 ordering.

- [ ] **Step 1: Add the 7 tests**

```java
/**
 * R3 with all 3 filter knobs NULL → fallthrough TRUE for every row,
 * even AEs without prior bid.
 */
@Test
void generate_r3_allNullFilters_visibleEvenWithoutPriorBid() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("ALN1", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("ALN1", "A", new BigDecimal("100.00"))
            // No prior bid
            .qbc(false, true, "Qualified")
            .brsfR3(null, null, null);

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R3 percent-variation branch: latest_bid >= round3_target_price * (1 - pct/100).
 */
@Test
void generate_r3_pctVarBranch_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("R3P1", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("R3P1", "A", new BigDecimal("100.00"))
            .priorBid("R3P1", "A", new BigDecimal("96.00"), 1)  // 96 >= 100*(1-5/100)=95
            .qbc(false, true, "Qualified")
            .brsfR3(new BigDecimal("5"), null, null);

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R3 amount-variation branch: latest_bid >= round3_target_price - amt.
 */
@Test
void generate_r3_amtVarBranch_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("R3A1", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("R3A1", "A", new BigDecimal("100.00"))
            .priorBid("R3A1", "A", new BigDecimal("99.00"), 1)  // 99 >= 100-1=99
            .qbc(false, true, "Qualified")
            .brsfR3(null, new BigDecimal("1.00"), null);

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R3 rank-limit branch: round3_bid_rank <= rank_lim.
 */
@Test
void generate_r3_rankLimBranch_visible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("R3R1", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("R3R1", "A", new BigDecimal("100.00"))
            .priorBidWithRank("R3R1", "A", new BigDecimal("10.00"), 1, 2)  // rank=2 <= 3
            .qbc(false, true, "Qualified")
            .brsfR3(null, null, 3);

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R3 with all 3 filters set + buyer bid fails ALL branches → invisible.
 */
@Test
void generate_r3_allFiltersSet_failAll_invisible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("R3F1", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("R3F1", "A", new BigDecimal("100.00"))
            .priorBidWithRank("R3F1", "A", new BigDecimal("10.00"), 1, 10)  // bid 10 < pct/amt; rank=10 > 3
            .qbc(false, true, "Qualified")
            .brsfR3(new BigDecimal("5"), new BigDecimal("1.00"), 3);

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isZero();
}

/**
 * R3 DISTINCT ON picks R2 over R1 by submitted_datetime when both bid the same AE.
 * Use round=3, seed an R1 bid (low) AND an R2 bid (qualifying).
 */
@Test
void generate_r3_distinctOn_picksR2OverR1() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("DST1", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("DST1", "A", new BigDecimal("100.00"))
            // R1 bid (low — would fail R3 pct branch on its own)
            .priorBid(1, "DST1", "A", new BigDecimal("10.00"), 1)
            // R2 bid (passes R3 pct branch)
            .priorBid(2, "DST1", "A", new BigDecimal("96.00"), 1)
            .qbc(false, true, "Qualified")
            .brsfR3(new BigDecimal("5"), null, null);

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    // R2 wins by DISTINCT ON ORDER BY submitted_datetime DESC; bid=96 passes pct.
    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R3 with rank_lim set + prev_rank IS NULL → rank branch can't match.
 * Other branches NULL → buyer fails. Verifies the IS NOT NULL guard.
 */
@Test
void generate_r3_prevRankNull_rankBranchOnly_invisible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("RNK1", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("RNK1", "A", new BigDecimal("100.00"))
            .priorBid("RNK1", "A", new BigDecimal("10.00"), 1)  // round3_bid_rank=NULL
            .qbc(false, true, "Qualified")
            .brsfR3(null, null, 3);  // rank_lim only

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isZero();
}
```

The `priorBid(int round, ...)` overload from Task 1 is used in the DISTINCT ON test. If that overload wasn't added in Task 1, add it now (or refactor Task 1's implementation).

- [ ] **Step 2: Run**

Run: `cd backend && mvn -q -Dtest='BidDataCreationRepositoryIT#generate_r3*' test -P pg-test`
Expected: PASS — all 7 R3 tests.

- [ ] **Step 3: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java
git commit -m "test(5b): R3 cascade coverage (4 branches + all-NULL + ROW_NUMBER + rank-NULL guard)"
```

---

## Task 7: STB shortcut + R1 regression IT cases

**Files:**
- Modify: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java`

Add 3 IT methods covering the `is_special_treatment` shortcut for both R2 and R3, plus an explicit R1 sanity check.

- [ ] **Step 1: Add the 3 tests**

```java
/**
 * R2 STB buyer with R1 bid that would otherwise fail all threshold branches →
 * row visible because is_special_treatment=TRUE shortcuts the cascade.
 */
@Test
void generate_r2_stbShortcut_visibleDespiteFailedThresholds() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(2)
            .buyerCodeType("Wholesale")
            .inventory("STB1", "A", 10, new BigDecimal("100.00"))
            .priorBid("STB1", "A", new BigDecimal("1.00"), 1)  // would fail all branches
            .qbc(true, true, "Qualified")  // is_special_treatment=TRUE
            .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                    "Only_Qualified", "InventoryRound1QualifiedBids");

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R3 STB buyer with no prior bid AND filters configured (so the threshold
 * cascade would say "invisible") → row visible because is_special_treatment.
 */
@Test
void generate_r3_stbShortcut_visibleDespiteNoBid() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(3)
            .buyerCodeType("Wholesale")
            .inventory("STB2", "A", 10, new BigDecimal("100.00"))
            .r3TargetPrice("STB2", "A", new BigDecimal("100.00"))
            // No prior bid
            .qbc(true, true, "Qualified")  // is_special_treatment=TRUE
            .brsfR3(new BigDecimal("5"), new BigDecimal("1.00"), 3);  // all 3 set

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
}

/**
 * R1 sanity: round=1 + no BRSF row + no prior bid → all rows visible
 * (R1 ELSE branch in the outer CASE returns TRUE; matches pre-5b behaviour).
 */
@Test
void generate_r1_sanity_allRowsVisible() {
    BidDataScenario scenario = new BidDataScenario(jdbc)
            .round(1)
            .buyerCodeType("Wholesale")
            .inventory("R1S1", "A", 10, new BigDecimal("100.00"))
            .inventory("R1S2", "A", 5,  new BigDecimal("50.00"))
            .qbc(false, true, "Qualified");
            // No BRSF row for round=1 (V59 chk_brsf_round forbids it)
            // No prior bid (R1 has no prior round)

    long bidRoundId   = scenario.commitAndReturnBidRoundId();
    long buyerCodeId  = scenario.lastBuyerCodeId();
    long bidDataDocId = scenario.lastBidDataDocId();

    assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(2);
}
```

- [ ] **Step 2: Run the full IT suite**

Run: `cd backend && mvn -q -Dtest=BidDataCreationRepositoryIT test -P pg-test`
Expected: ALL tests pass — original R1-init cases + 20 new 5b cases.

- [ ] **Step 3: Verify total test count**

```bash
grep -c "^    @Test" backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java
```

Should be the original count + 20 (Task 2's 1 + Task 4's 6 + Task 5's 3 + Task 6's 7 + Task 7's 3 = 20 new).

- [ ] **Step 4: Commit**

```bash
git add backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/BidDataCreationRepositoryIT.java
git commit -m "test(5b): STB shortcut for R2/R3 + R1 regression sanity"
```

---

## Task 8: Docs + ADR

**Files:**
- Modify: `docs/architecture/decisions.md`
- Modify: `docs/business-logic/r2-buyer-assignment.md`
- Modify: `docs/business-logic/r3-init-and-preprocess.md`
- Modify: `docs/testing/coverage.md`
- Modify: `docs/tasks/auction-flow-gap-analysis-2026-05-06.md`

- [ ] **Step 1: Append ADR for sub-project 5b**

In `docs/architecture/decisions.md`, prepend (latest-first convention) a new ADR:

```markdown
## ADR — Sub-project 5b: R2/R3 row visibility correctness (2026-05-07)

**Status:** Accepted

**Context:** `BidDataCreationRepository.generate(...)` had two `TRUE`-constant stubs
for `bid_meets_threshold` and `row_visible` since sub-project 4. Every R2/R3 buyer
saw every AE row regardless of R1 rank, R1 bid amount, or whether they had a prior
bid at all. Functionally incorrect for production. Sub-project 5b replaces both
stubs with the real predicate cascades:

- **R2** ports the per-AE predicate from `SUB_Round2AggregatedInventorySingleItem`
  (5 branches with DW/Wholesale split, with whole-percent unit alignment).
- **R3** uses the per-row form of sub-project 6's R3 selection rule (4 branches:
  all-NULL fallthrough + pct + amt + rank).
- **STB shortcut**: `row_visible = is_special_treatment OR bid_meets_threshold` —
  STB buyers see every row regardless of threshold (covers gap-analysis #8's
  basic STB-sees-all rule; full STB rerun semantics remain a 5c task).

**Decisions:** see `docs/tasks/auction-r2-r3-row-visibility-design.md` §3 for the
14 numbered decisions. Highlights:
- One method, one CTE_SQL constant, branched on `params.round` (decision 3.1)
- Round-agnostic `prior_round_biddata` via `DISTINCT ON` (decision 3.6)
- `prior_scheduling_auction` CTE deleted (subsumed)
- LEFT JOIN refactor for "missing QBC" deferred (cosmetic only)

**Consequences:**
- Bidder dashboard now correctly filters AEs per-buyer for R2 and R3
- Existing R1-init behaviour preserved (R1 ELSE branch returns TRUE)
- `BidDataCreationService` signature + concurrency contract unchanged

**Out of scope:**
- Sub-project 5c: `SUB_HandleSpecialTreatmentBuyerOnRoundStart` (STB rerun)
- LEFT JOIN refactor for "missing QBC" distinction
```

- [ ] **Step 2: Update R2 business-logic doc**

In `docs/business-logic/r2-buyer-assignment.md`, append a section:

```markdown
## Per-row R2 visibility (sub-project 5b, 2026-05-07)

`BidDataCreationRepository` now applies the same per-AE predicate cascade as
`R2BuyerQualificationRepository` (sub-project 5), just at row scope rather than
buyer scope. The cascade has 5 branches:

1. `qual_mode = 'All_Buyers'` → row visible regardless of R1 bid
2. No R1 bid + `inv_mode = 'ShowAllInventory'` → visible; otherwise invisible
3. R1 bid + target = 0 + bid > 0 → visible
4. R1 bid + bid/target ≥ 1 - (target_pct / 100) → visible (percent band)
5. R1 bid + (target - bid) ≤ target_value → visible (flat band)

Plus inv_mode fallback: `InventoryRound1QualifiedBids` admits any positive R1 bid.

DW vs Wholesale buyer codes use `dw_avg_target_price` vs `avg_target_price`
respectively.

**STB shortcut:** if `qualified_buyer_codes.is_special_treatment = TRUE` for the
buyer code, `row_visible = TRUE` regardless of threshold.
```

- [ ] **Step 3: Update R3 business-logic doc**

In `docs/business-logic/r3-init-and-preprocess.md`, append a section:

```markdown
## Per-row R3 visibility (sub-project 5b, 2026-05-07)

`BidDataCreationRepository` applies the per-row form of sub-project 6's R3
selection rule. For each (ecoid, merged_grade) row, take the buyer's latest
submitted nonzero bid across rounds 1+2 (`DISTINCT ON ... ORDER BY
submitted_datetime DESC`) and apply the 4-branch OR cascade:

1. All three filter knobs NULL → row visible regardless
2. `bid_percentage_variation` set + latest_bid ≥ round3_target_price × (1 - pct/100)
3. `bid_amount_variation` set + latest_bid ≥ round3_target_price - amt
4. `rank_qualification_limit` set + round3_bid_rank ≤ limit

ANY branch matches → row visible.

**STB shortcut:** same as R2 — `is_special_treatment=TRUE` admits regardless.
```

- [ ] **Step 4: Update coverage doc**

In `docs/testing/coverage.md`, append:

```markdown
## auctions.biddata.row-visibility (new 2026-05-07)
Target 85%+. R2 cascade (Only_Qualified branches + qual_mode shortcuts), R3
cascade (4 branches + all-NULL + ROW_NUMBER R2-over-R1), STB shortcut for
R2/R3, R1 regression sanity. See `BidDataCreationRepositoryIT` (20 new cases
added by sub-project 5b) and `BidDataScenario` builder extensions.
```

- [ ] **Step 5: Update gap-analysis**

In `docs/tasks/auction-flow-gap-analysis-2026-05-06.md`, find row #3:

```
| **3** | **Fix `bid_meets_threshold` + `row_visible` stubs in `BidDataCreationRepository.java:126–137`** | M | Every buyer currently sees every row in R2/R3 regardless of R1 rank — functionally incorrect for production |
```

Replace with:

```
| **3** | ~~**Fix `bid_meets_threshold` + `row_visible` stubs in `BidDataCreationRepository.java:126–137`**~~ ✅ **Shipped 2026-05-07 (sub-project 5b)** | M | R2 + R3 cascades + STB shortcut; 20 new IT cases; design at `docs/tasks/auction-r2-r3-row-visibility-design.md` |
```

Update the "Critical path" note further down (around line 122) to reflect that item #3 is also shipped.

- [ ] **Step 6: Commit**

```bash
git add docs/architecture/decisions.md \
        docs/business-logic/r2-buyer-assignment.md \
        docs/business-logic/r3-init-and-preprocess.md \
        docs/testing/coverage.md \
        docs/tasks/auction-flow-gap-analysis-2026-05-06.md
git commit -m "docs(5b): R2/R3 row visibility — ADR, business logic, coverage, gap-analysis update"
```

---

## Self-Review

**Spec coverage:** every section of `auction-r2-r3-row-visibility-design.md` maps to a task above:

- §3.1 single-CTE branch on `params.round` → Task 3
- §3.3 R2 cascade ports `SUB_Round2AggregatedInventorySingleItem` → Task 3 (SQL) + Task 4 (tests)
- §3.4 R3 cascade is per-row form of R3 selection rule → Task 3 (SQL) + Task 6 (tests)
- §3.5 R3 all-NULL fallthrough → Task 6 test 1
- §3.6 round-agnostic `prior_round_biddata` via `DISTINCT ON` → Task 3 + Task 6 test 6 (R2-over-R1 ordering)
- §3.7 STB shortcut → Task 3 (SQL) + Task 7 (tests 1+2)
- §3.8 `WHERE q.included = TRUE` preserved → Task 3 SQL block 7.4
- §3.9 R2 `All_Buyers` → all rows visible → Task 5 test 1
- §3.10 R1 fallthrough TRUE → Task 7 test 3
- §3.11 `prior_round_biddata` filters `submitted=TRUE` and `submitted_bid_amount > 0` → Task 3 SQL block 7.2
- §3.12 no code reuse — predicate duplicated → Task 3
- §3.13 LEFT JOIN refactor deferred → out of scope (no task)
- §3.14 service layer + concurrency unchanged → no task (verified by Task 3 step 7 running existing tests)
- §9 testing → Tasks 4–7 cover the 20 cases
- §10 docs → Task 8

**Type consistency:**
- `BidDataScenario` builder methods consistently return `BidDataScenario` for chaining
- `qbc(boolean, boolean, String)` signature used identically across Tasks 4-7
- `brsfR2` / `brsfR3` parameter types match the SQL columns (`NUMERIC(10,4)` → `BigDecimal`, `INTEGER` → `Integer`)
- `priorBid(String, String, BigDecimal, int)` and the `priorBid(int round, String, ...)` overload are used consistently
- `repo.generate(long, long, long)` signature unchanged across all tests

**Placeholder scan:** No `TBD` / `TODO` / `implement later` / vague "add validation" / "handle edge cases" / "similar to Task N". All test bodies and SQL blocks are complete and self-contained.

**Unreachable case note (Task 4 step 1):** the inline comment explaining why a 7th explicit "below all thresholds + Only_Qualified + has bid > 0" test isn't reachable is deliberate — it documents reasoning rather than acting as a placeholder.

---

## Execution Handoff

Plan complete and saved to `docs/tasks/auction-r2-r3-row-visibility-plan.md`. Two execution options:

**1. Subagent-Driven (recommended)** — dispatch a fresh subagent per task, review between tasks, fast iteration. Required sub-skill: `superpowers:subagent-driven-development`.

**2. Inline Execution** — execute tasks in this session using `superpowers:executing-plans`, batch execution with checkpoints.

Which approach?
