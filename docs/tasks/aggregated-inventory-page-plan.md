# Aggregated Inventory Page Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL — Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rebuild the legacy Mendix `AuctionUI.PG_AggregatedInventory` page in the modern Next.js + Spring Boot stack with 100% UI parity (sidebar, KPI strip, week selector, filter inputs, column order, pagination, edit popup, Excel export, Create Auction button) and 100% functional parity (week-scoped read, row edit, totals recomputation, Excel download).

**Architecture:**
- Backend: New `auctions` service tier on top of the existing V60 `auctions.aggregated_inventory` / `auctions.aggregated_inventory_totals` schema. Service reads via paginated native SQL (follows `BuyerOverviewService` pattern) and joins `mdm.week`, `mdm.brand`, `mdm.model`, `mdm.carrier` with LEFT JOIN for the dimension text labels. Row edit is a `PUT` through a focused service method. Totals are read from `aggregated_inventory_totals` (per-week precomputed row) to match Mendix `AggregatedInventoryTotals` entity.
- Frontend: New `/admin/auctions-data-center/inventory` page mirrors the `buyers` overview pattern — debounced (500ms) filter inputs, `apiFetch` + URL params, plain CSS module matching Mendix `#407874 / #112d32 / #F7F7F7 / Trebuchet MS` tokens. Week selector is a `<select>` populated from `/weeks/active` and defaults to the week whose `weekEndDateTime > now()`.
- Async/events: Excel export is a synchronous streaming endpoint (Apache POI) — short enough to not need the post-commit event pattern. Create Auction is a stub in Phase 1 (wired in Phase 2 when the auction scheduling module is ported).

**Tech Stack:**
- Backend: Java 21, Spring Boot 3, Spring Data JPA (native queries for aggregation), Hibernate, PostgreSQL 15, Flyway (schema already in V60), Apache POI (`poi-ooxml`) for Excel export, JUnit 5 + AssertJ + Mockito + MockMvc.
- Frontend: Next.js 16 (App Router), TypeScript strict, Zod (schema validation at boundary), CSS Modules (no Tailwind for this page), Playwright for pixel-parity visual regression.

---

## Source References

Read these before starting any task:

| Concern | File |
|---|---|
| Mendix page widgets | `migration_context/frontend/components/Pages_Page/PG_AggregatedInventory.md` |
| Mendix edit popup | `migration_context/frontend/components/Pages_Page/AggregatedInventory_NewEdit.md` |
| Mendix edit save flow | `migration_context/backend/ACT_AggregateInventory_UpdateByAdmin.md` |
| Mendix filter rebuild | `migration_context/backend/services/SUB_BuildAggregatedInventoryFilters.md` |
| Mendix load flow | `migration_context/backend/services/SUB_LoadAggregatedInventory.md` |
| Mendix helper bootstrap | `migration_context/backend/DS_GetOrCreateAggregatedInventoryHelper.md` |
| Mendix totals bootstrap | `migration_context/backend/DS_GetOrCreateAggregatedInventoryTotalsByWeek.md` |
| Mendix navigation (entry) | `migration_context/backend/services/SUB_NavigateToAggregatedInventoryPage.md` |
| Target schema | `backend/src/main/resources/db/migration/V60__auctions_aggregated_inventory.sql` |
| Week schema + seed | `backend/src/main/resources/db/migration/V58__create_auctions_schema_and_core.sql`, `V65__seed_mdm_week.sql` |
| Reference service pattern | `backend/src/main/java/com/ecoatm/salesplatform/service/BuyerOverviewService.java` |
| Reference controller pattern | `backend/src/main/java/com/ecoatm/salesplatform/controller/BuyerOverviewController.java` |
| Reference frontend pattern | `frontend/src/app/(dashboard)/buyers/page.tsx`, `frontend/src/app/(dashboard)/buyers/buyers.module.css` |
| Mendix styling tokens | `migration_context/styling/EcoAtm.css` |

**QA reference for pixel parity** (captured via Playwright):
- URL: `https://buy-qa.ecoatmdirect.com/index.html` → Admin → Inventory
- Columns (left→right): Product ID (Equal filter), Grades (Contains), Brand (Contains), Model (Contains), Model Name (Contains), Carrier (Contains), DW Qty (Equal), DW Target Price (Equal), Total Qty (Equal), Target Price (Equal), + Column selector.
- KPI strip order (left→right): Total Quantity, Total Payout, Average Target Price, DW Total Quantity, DW Total Payout, DW Average Target Price.
- Week selector: shows `"2026 / Wk17"` format, refresh icon right of it.
- Buttons: `Create Auction`, `Refresh`. Footer: `Download` (Excel export).
- Pagination: `"Currently showing 1 to 20 of 11735"` text + first/prev/next/last buttons.

---

## File Structure

Each file has one clear responsibility. Files that change together live together.

### Backend — new files

```
backend/src/main/java/com/ecoatm/salesplatform/
├── model/
│   ├── mdm/
│   │   └── Week.java                                    — JPA entity for mdm.week
│   └── auctions/                                         (NEW package)
│       ├── AggregatedInventory.java                     — JPA entity (all V60 columns)
│       └── AggregatedInventoryTotals.java               — JPA entity for totals
├── repository/
│   ├── mdm/
│   │   └── WeekRepository.java                          — findById + findCurrentWeek()
│   └── auctions/                                         (NEW package)
│       ├── AggregatedInventoryRepository.java           — findById, save
│       └── AggregatedInventoryTotalsRepository.java     — findByWeekId
├── dto/
│   ├── AggregatedInventoryRow.java                      — flat DTO for grid rows
│   ├── AggregatedInventoryPageResponse.java             — {content, page, pageSize, totalElements, totalPages}
│   ├── AggregatedInventoryTotalsResponse.java           — KPI strip payload
│   ├── AggregatedInventoryUpdateRequest.java            — {mergedGrade?, datawipe?, totalQuantity?, dwTotalQuantity?}
│   └── WeekOption.java                                  — {id, weekDisplay, weekStartDateTime, weekEndDateTime}
├── service/
│   └── auctions/                                         (NEW package)
│       ├── AggregatedInventoryService.java              — search(), getTotals(), updateRow()
│       └── AggregatedInventoryExcelExporter.java        — POI streaming writer
└── controller/
    └── AggregatedInventoryController.java               — REST endpoints under /api/v1/admin/inventory
```

### Backend — tests

```
backend/src/test/java/com/ecoatm/salesplatform/
├── service/auctions/
│   ├── AggregatedInventoryServiceTest.java              — mocks EntityManager + repos
│   └── AggregatedInventoryExcelExporterTest.java        — renders workbook to byte[], asserts cells
└── controller/
    └── AggregatedInventoryControllerTest.java           — MockMvc with @WebMvcTest
```

### Frontend — new files

```
frontend/src/app/(dashboard)/admin/auctions-data-center/
├── inventory/
│   ├── page.tsx                                         — Inventory grid page
│   └── inventory.module.css                             — Mendix parity styling
└── page.tsx                                             — (existing stub — add link to inventory)

frontend/src/lib/
└── aggregatedInventory.ts                               — Zod schemas + fetch helpers (type-safe boundary)
```

### Frontend — tests

```
frontend/tests/e2e/
└── aggregated-inventory.spec.ts                         — Playwright journey (filter, paginate, edit, export)
```

### Docs updates

```
docs/
├── api/rest-endpoints.md                                — append "Aggregated Inventory" section
├── architecture/decisions.md                            — add 2026-04-17 ADR entry
└── app-metadata/modules.md                              — add "auctions" module row
```

---

## Phase Layout

| Phase | Scope | Commit prefix |
|---|---|---|
| 1 | Backend JPA entities + repositories (Week, AggregatedInventory, Totals) | `feat` |
| 2 | Week-active endpoint + frontend week selector skeleton | `feat` |
| 3 | Backend paginated search + totals endpoints | `feat` |
| 4 | Frontend grid with filters, KPI strip, pagination | `feat` |
| 5 | Edit popup (backend PUT + frontend modal) | `feat` |
| 6 | Excel export | `feat` |
| 7 | Create Auction stub + Refresh button wiring | `feat` |
| 8 | Docs, ADR, E2E test, pixel-parity screenshot sweep | `docs` + `test` |

Target: commit after each step — never batch more than 5 minutes of work into one commit.

---

## Phase 1 — Backend JPA entities + repositories

### Task 1.1: Week entity

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/mdm/Week.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/mdm/WeekRepositoryTest.java`

- [ ] **Step 1: Write the failing repository test**

```java
// WeekRepositoryTest.java
package com.ecoatm.salesplatform.repository.mdm;

import com.ecoatm.salesplatform.model.mdm.Week;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class WeekRepositoryTest {

    @Autowired private WeekRepository weekRepository;

    @Test
    @DisplayName("findAll returns at least one week from V65 seed")
    void findAll_afterFlywaySeed_returnsRows() {
        assertThat(weekRepository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("findCurrentWeek returns the week whose end datetime is in the future")
    void findCurrentWeek_returnsOne() {
        Optional<Week> current = weekRepository.findCurrentWeek();
        assertThat(current).isPresent();
        assertThat(current.get().getWeekEndDateTime()).isAfter(java.time.Instant.now());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=WeekRepositoryTest`
Expected: FAIL with "cannot find symbol `Week`" / `WeekRepository`.

- [ ] **Step 3: Write `Week` entity**

```java
// Week.java
package com.ecoatm.salesplatform.model.mdm;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "week", schema = "mdm")
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")        private Long legacyId;
    @Column(name = "week_id")          private Long weekId;
    @Column(name = "year")             private Integer year;
    @Column(name = "week_number")      private Integer weekNumber;
    @Column(name = "week_start_datetime") private Instant weekStartDateTime;
    @Column(name = "week_end_datetime")   private Instant weekEndDateTime;
    @Column(name = "week_display")     private String weekDisplay;
    @Column(name = "week_display_short") private String weekDisplayShort;
    @Column(name = "auction_data_purged") private boolean auctionDataPurged;

    public Long getId() { return id; }
    public Long getWeekId() { return weekId; }
    public Integer getYear() { return year; }
    public Integer getWeekNumber() { return weekNumber; }
    public Instant getWeekStartDateTime() { return weekStartDateTime; }
    public Instant getWeekEndDateTime() { return weekEndDateTime; }
    public String getWeekDisplay() { return weekDisplay; }
    public String getWeekDisplayShort() { return weekDisplayShort; }
    public boolean isAuctionDataPurged() { return auctionDataPurged; }
}
```

- [ ] **Step 4: Write `WeekRepository`**

```java
// WeekRepository.java
package com.ecoatm.salesplatform.repository.mdm;

import com.ecoatm.salesplatform.model.mdm.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WeekRepository extends JpaRepository<Week, Long> {

    /**
     * Mendix parity: SUB_NavigateToAggregatedInventoryPage retrieves the week
     * whose weekEndDateTime > CurrentDateTime, ordered by start ascending.
     * Returns the first such week so the page opens on the active auction week.
     */
    @Query("SELECT w FROM Week w WHERE w.weekEndDateTime > CURRENT_TIMESTAMP ORDER BY w.weekStartDateTime ASC")
    List<Week> findFutureWeeks();

    default Optional<Week> findCurrentWeek() {
        return findFutureWeeks().stream().findFirst();
    }

    List<Week> findAllByOrderByWeekStartDateTimeDesc();
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `mvn -pl backend test -Dtest=WeekRepositoryTest`
Expected: PASS (2 tests).

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/mdm/Week.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/mdm/WeekRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/mdm/WeekRepositoryTest.java
git commit -m "feat: add Week JPA entity and repository"
```

---

### Task 1.2: AggregatedInventory + Totals entities

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/AggregatedInventory.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/model/auctions/AggregatedInventoryTotals.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryRepository.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryTotalsRepository.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryRepositoryTest.java`

- [ ] **Step 1: Write the failing repository test**

```java
// AggregatedInventoryRepositoryTest.java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class AggregatedInventoryRepositoryTest {

    @Autowired private AggregatedInventoryRepository repo;

    @Test
    @DisplayName("save and findById round-trips an AggregatedInventory row")
    void save_findById_roundTrip() {
        AggregatedInventory a = new AggregatedInventory();
        a.setEcoid2("999999");
        a.setName("Test Device");
        a.setBrand("TestBrand");
        a.setModel("TestModel");
        a.setCarrier("TestCarrier");
        a.setMergedGrade("A_YYY");
        a.setTotalQuantity(5);
        a.setDwTotalQuantity(2);
        a.setAvgTargetPrice(new java.math.BigDecimal("12.3400"));

        AggregatedInventory saved = repo.save(a);
        assertThat(saved.getId()).isNotNull();
        assertThat(repo.findById(saved.getId())).get()
                .extracting(AggregatedInventory::getEcoid2).isEqualTo("999999");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryRepositoryTest`
Expected: FAIL — `AggregatedInventory` and `AggregatedInventoryRepository` symbols unresolved.

- [ ] **Step 3: Write `AggregatedInventory` entity**

Match V60 column names exactly. Only the fields the page reads/writes need getters — the extras (round 2/3, PO max, factors) can be mapped but left unused for Phase 1. Use `@Column(name = …)` for every column because JPA default snake→camel mapping differs.

```java
// AggregatedInventory.java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "aggregated_inventory", schema = "auctions")
public class AggregatedInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")   private Long legacyId;
    @Column(name = "ecoid2")      private String ecoid2;
    @Column(name = "name")        private String name;
    @Column(name = "device_id")   private String deviceId;
    @Column(name = "category")    private String category;

    @Column(name = "brand")       private String brand;
    @Column(name = "model")       private String model;
    @Column(name = "carrier")     private String carrier;

    @Column(name = "brand_id")    private Long brandId;
    @Column(name = "model_id")    private Long modelId;
    @Column(name = "carrier_id")  private Long carrierId;
    @Column(name = "week_id")     private Long weekId;

    @Column(name = "merged_grade")               private String mergedGrade;
    @Column(name = "datawipe")                   private boolean datawipe;
    @Column(name = "is_total_quantity_modified") private boolean totalQuantityModified;
    @Column(name = "is_deprecated")              private boolean deprecated;

    @Column(name = "total_quantity")     private int totalQuantity;
    @Column(name = "dw_total_quantity")  private int dwTotalQuantity;

    @Column(name = "avg_payout")         private BigDecimal avgPayout;
    @Column(name = "total_payout")       private BigDecimal totalPayout;
    @Column(name = "dw_avg_payout")      private BigDecimal dwAvgPayout;
    @Column(name = "dw_total_payout")    private BigDecimal dwTotalPayout;

    @Column(name = "avg_target_price")     private BigDecimal avgTargetPrice;
    @Column(name = "dw_avg_target_price")  private BigDecimal dwAvgTargetPrice;

    @Column(name = "created_date")  private Instant createdDate;
    @Column(name = "changed_date")  private Instant changedDate;

    // --- getters/setters (all fields) ---
    public Long getId() { return id; }
    public String getEcoid2() { return ecoid2; }
    public void setEcoid2(String v) { ecoid2 = v; }
    public String getName() { return name; }
    public void setName(String v) { name = v; }
    public String getBrand() { return brand; }
    public void setBrand(String v) { brand = v; }
    public String getModel() { return model; }
    public void setModel(String v) { model = v; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String v) { carrier = v; }
    public Long getWeekId() { return weekId; }
    public void setWeekId(Long v) { weekId = v; }
    public String getMergedGrade() { return mergedGrade; }
    public void setMergedGrade(String v) { mergedGrade = v; }
    public boolean isDatawipe() { return datawipe; }
    public void setDatawipe(boolean v) { datawipe = v; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int v) { totalQuantity = v; }
    public int getDwTotalQuantity() { return dwTotalQuantity; }
    public void setDwTotalQuantity(int v) { dwTotalQuantity = v; }
    public BigDecimal getAvgTargetPrice() { return avgTargetPrice; }
    public void setAvgTargetPrice(BigDecimal v) { avgTargetPrice = v; }
    public BigDecimal getDwAvgTargetPrice() { return dwAvgTargetPrice; }
    public void setDwAvgTargetPrice(BigDecimal v) { dwAvgTargetPrice = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { changedDate = v; }
    public boolean isTotalQuantityModified() { return totalQuantityModified; }
    public void setTotalQuantityModified(boolean v) { totalQuantityModified = v; }
}
```

- [ ] **Step 4: Write `AggregatedInventoryTotals` entity**

```java
// AggregatedInventoryTotals.java
package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "aggregated_inventory_totals", schema = "auctions")
public class AggregatedInventoryTotals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "week_id")           private Long weekId;
    @Column(name = "total_quantity")    private int totalQuantity;
    @Column(name = "dw_total_quantity") private int dwTotalQuantity;
    @Column(name = "total_payout")      private BigDecimal totalPayout;
    @Column(name = "dw_total_payout")   private BigDecimal dwTotalPayout;
    @Column(name = "device_count")      private int deviceCount;

    public Long getId() { return id; }
    public Long getWeekId() { return weekId; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getDwTotalQuantity() { return dwTotalQuantity; }
    public BigDecimal getTotalPayout() { return totalPayout; }
    public BigDecimal getDwTotalPayout() { return dwTotalPayout; }
    public int getDeviceCount() { return deviceCount; }
}
```

- [ ] **Step 5: Write both repositories**

```java
// AggregatedInventoryRepository.java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AggregatedInventoryRepository extends JpaRepository<AggregatedInventory, Long> {
}
```

```java
// AggregatedInventoryTotalsRepository.java
package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.AggregatedInventoryTotals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AggregatedInventoryTotalsRepository extends JpaRepository<AggregatedInventoryTotals, Long> {
    Optional<AggregatedInventoryTotals> findByWeekId(Long weekId);
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryRepositoryTest`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/model/auctions/AggregatedInventory.java \
        backend/src/main/java/com/ecoatm/salesplatform/model/auctions/AggregatedInventoryTotals.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryRepository.java \
        backend/src/main/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryTotalsRepository.java \
        backend/src/test/java/com/ecoatm/salesplatform/repository/auctions/AggregatedInventoryRepositoryTest.java
git commit -m "feat: add AggregatedInventory and totals JPA entities"
```

---

## Phase 2 — Week selector endpoint + frontend skeleton

### Task 2.1: `GET /api/v1/admin/inventory/weeks`

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/WeekOption.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/controller/AggregatedInventoryController.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/controller/AggregatedInventoryControllerTest.java`

- [ ] **Step 1: Write the failing controller test (week list only)**

```java
// AggregatedInventoryControllerTest.java
package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AggregatedInventoryController.class)
class AggregatedInventoryControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private AggregatedInventoryService service;
    @MockBean private WeekRepository weekRepo;

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /weeks returns weeks ordered by start desc")
    void weeks_returnsOrderedList() throws Exception {
        Week w = new Week();
        // reflection is fine here; entity has no setters for the test-only fields
        java.lang.reflect.Field idField = Week.class.getDeclaredField("id");
        idField.setAccessible(true); idField.set(w, 100L);
        java.lang.reflect.Field dispField = Week.class.getDeclaredField("weekDisplay");
        dispField.setAccessible(true); dispField.set(w, "2026 / Wk17");
        java.lang.reflect.Field startField = Week.class.getDeclaredField("weekStartDateTime");
        startField.setAccessible(true); startField.set(w, Instant.parse("2026-04-20T00:00:00Z"));
        java.lang.reflect.Field endField = Week.class.getDeclaredField("weekEndDateTime");
        endField.setAccessible(true); endField.set(w, Instant.parse("2026-04-27T00:00:00Z"));

        when(weekRepo.findAllByOrderByWeekStartDateTimeDesc()).thenReturn(List.of(w));

        mvc.perform(get("/api/v1/admin/inventory/weeks"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].id").value(100))
           .andExpect(jsonPath("$[0].weekDisplay").value("2026 / Wk17"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryControllerTest`
Expected: FAIL — `AggregatedInventoryController` / `AggregatedInventoryService` / `WeekOption` unresolved.

- [ ] **Step 3: Write `WeekOption` DTO**

```java
// WeekOption.java
package com.ecoatm.salesplatform.dto;

import java.time.Instant;

public record WeekOption(
        Long id,
        String weekDisplay,
        Instant weekStartDateTime,
        Instant weekEndDateTime
) {}
```

- [ ] **Step 4: Write a stub `AggregatedInventoryService` (search/getTotals/update will be implemented in Phase 3)**

```java
// AggregatedInventoryService.java
package com.ecoatm.salesplatform.service.auctions;

import org.springframework.stereotype.Service;

@Service
public class AggregatedInventoryService {
    // methods added in Phase 3 — stub exists so the controller can wire it now.
}
```

- [ ] **Step 5: Write controller with `/weeks` endpoint only**

```java
// AggregatedInventoryController.java
package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.WeekOption;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.mdm.WeekRepository;
import com.ecoatm.salesplatform.service.auctions.AggregatedInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@PreAuthorize("hasAnyRole('Administrator','SalesOps')")
public class AggregatedInventoryController {

    private final AggregatedInventoryService service;
    private final WeekRepository weekRepository;

    public AggregatedInventoryController(AggregatedInventoryService service, WeekRepository weekRepository) {
        this.service = service;
        this.weekRepository = weekRepository;
    }

    @GetMapping("/weeks")
    public ResponseEntity<List<WeekOption>> listWeeks() {
        List<Week> weeks = weekRepository.findAllByOrderByWeekStartDateTimeDesc();
        List<WeekOption> options = weeks.stream()
                .map(w -> new WeekOption(w.getId(), w.getWeekDisplay(),
                        w.getWeekStartDateTime(), w.getWeekEndDateTime()))
                .toList();
        return ResponseEntity.ok(options);
    }
}
```

- [ ] **Step 6: Run test to verify it passes**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryControllerTest`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/WeekOption.java \
        backend/src/main/java/com/ecoatm/salesplatform/controller/AggregatedInventoryController.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryService.java \
        backend/src/test/java/com/ecoatm/salesplatform/controller/AggregatedInventoryControllerTest.java
git commit -m "feat: add week list endpoint for inventory page"
```

---

### Task 2.2: Frontend page skeleton with week selector

**Files:**
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx`
- Create: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/inventory.module.css`
- Create: `frontend/src/lib/aggregatedInventory.ts`

- [ ] **Step 1: Write Zod schemas and fetch helpers**

```ts
// frontend/src/lib/aggregatedInventory.ts
import { z } from 'zod';
import { apiFetch } from './apiFetch';

export const WeekOptionSchema = z.object({
  id: z.number(),
  weekDisplay: z.string(),
  weekStartDateTime: z.string(),
  weekEndDateTime: z.string(),
});
export type WeekOption = z.infer<typeof WeekOptionSchema>;

export const InventoryRowSchema = z.object({
  id: z.number(),
  ecoid2: z.string(),
  mergedGrade: z.string().nullable(),
  brand: z.string().nullable(),
  model: z.string().nullable(),
  name: z.string().nullable(),
  carrier: z.string().nullable(),
  dwTotalQuantity: z.number(),
  dwAvgTargetPrice: z.number(),
  totalQuantity: z.number(),
  avgTargetPrice: z.number(),
});
export type InventoryRow = z.infer<typeof InventoryRowSchema>;

export const InventoryPageResponseSchema = z.object({
  content: z.array(InventoryRowSchema),
  page: z.number(),
  pageSize: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
});
export type InventoryPageResponse = z.infer<typeof InventoryPageResponseSchema>;

export const InventoryTotalsSchema = z.object({
  totalQuantity: z.number(),
  totalPayout: z.number(),
  averageTargetPrice: z.number(),
  dwTotalQuantity: z.number(),
  dwTotalPayout: z.number(),
  dwAverageTargetPrice: z.number(),
  lastSyncedAt: z.string().nullable(),
});
export type InventoryTotals = z.infer<typeof InventoryTotalsSchema>;

export async function fetchWeeks(): Promise<WeekOption[]> {
  const res = await apiFetch('/api/v1/admin/inventory/weeks');
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return z.array(WeekOptionSchema).parse(await res.json());
}
```

- [ ] **Step 2: Write CSS module (Mendix parity tokens)**

```css
/* inventory.module.css */
.page {
  padding: 1.25rem 1.5rem;
  background: #F7F7F7;
  color: #112d32;
  font-family: "Trebuchet MS", Arial, sans-serif;
}

.header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #112d32;
  margin: 0;
  margin-right: auto;
}

.weekSelect {
  height: 34px;
  padding: 0 .5rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  background: #fff;
  font: inherit;
  color: inherit;
  min-width: 140px;
}

.button {
  background: #407874;
  color: #fff;
  border: 0;
  padding: 0 .9rem;
  height: 34px;
  border-radius: 4px;
  font: inherit;
  cursor: pointer;
}

.button:hover { background: #356562; }

.buttonGhost {
  background: #fff;
  color: #407874;
  border: 1px solid #407874;
}

.lastSynced {
  font-size: .8rem;
  color: #6b6b6b;
}

.kpiStrip {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: .75rem;
  margin-bottom: 1rem;
}

.kpiCard {
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: .75rem 1rem;
}

.kpiLabel {
  font-size: .75rem;
  color: #6b6b6b;
  text-transform: none;
  margin: 0 0 .25rem;
}

.kpiValue {
  font-size: 1.35rem;
  font-weight: 600;
  color: #112d32;
  margin: 0;
}

.gridWrap {
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  overflow: auto;
}

.grid {
  width: 100%;
  border-collapse: collapse;
  font-size: .85rem;
}

.grid th {
  text-align: left;
  background: #f3f3f3;
  padding: .55rem .5rem;
  border-bottom: 1px solid #d7d7d7;
  font-weight: 600;
  color: #112d32;
  position: sticky; top: 0;
}

.grid td {
  padding: .45rem .5rem;
  border-bottom: 1px solid #eee;
}

.filterInput {
  width: 100%;
  padding: 2px 4px;
  height: 24px;
  border: 1px solid #cfcfcf;
  border-radius: 3px;
  font: inherit;
}

.pagination {
  display: flex;
  align-items: center;
  gap: .5rem;
  padding: .75rem 1rem;
  border-top: 1px solid #e5e5e5;
  background: #fafafa;
  color: #555;
  font-size: .85rem;
}
```

- [ ] **Step 3: Write `page.tsx` skeleton — header + week selector + empty KPI strip + empty table**

```tsx
// page.tsx
'use client';

import { useEffect, useState } from 'react';
import styles from './inventory.module.css';
import { fetchWeeks, type WeekOption } from '@/lib/aggregatedInventory';

export default function AggregatedInventoryPage() {
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [selectedWeekId, setSelectedWeekId] = useState<number | null>(null);
  const [lastSyncedAt, setLastSyncedAt] = useState<string | null>(null);

  useEffect(() => {
    fetchWeeks().then(list => {
      setWeeks(list);
      // Mendix parity: default to the first week whose end datetime is in the future.
      const now = Date.now();
      const current = list.find(w => new Date(w.weekEndDateTime).getTime() > now);
      setSelectedWeekId((current ?? list[0])?.id ?? null);
    }).catch(() => setWeeks([]));
  }, []);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>Inventory</h2>
        <select
          className={styles.weekSelect}
          value={selectedWeekId ?? ''}
          onChange={e => setSelectedWeekId(Number(e.target.value))}
        >
          {weeks.map(w => (
            <option key={w.id} value={w.id}>{w.weekDisplay}</option>
          ))}
        </select>
        <button className={styles.button} type="button">Create Auction</button>
        <button className={styles.buttonGhost} type="button">Refresh</button>
        <span className={styles.lastSynced}>
          Last synced: {lastSyncedAt ?? '—'}
        </span>
      </header>

      <section className={styles.kpiStrip} aria-label="Inventory totals">
        {/* Filled in Phase 4 */}
      </section>

      <div className={styles.gridWrap}>
        {/* Filled in Phase 4 */}
      </div>
    </div>
  );
}
```

- [ ] **Step 4: Start the dev server and verify the page renders the week dropdown**

Run: `cd frontend && npm run dev` — then open `http://localhost:3000/admin/auctions-data-center/inventory` after logging in as `admin@test.com / Admin123!`.
Expected: page renders, week dropdown is populated with "2026 / Wk17" and earlier weeks.

- [ ] **Step 5: Commit**

```bash
git add frontend/src/app/\(dashboard\)/admin/auctions-data-center/inventory \
        frontend/src/lib/aggregatedInventory.ts
git commit -m "feat: scaffold inventory page with week selector"
```

---

## Phase 3 — Backend paginated search + totals

### Task 3.1: Service `search()` method

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryService.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryRow.java`
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryPageResponse.java`
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryServiceTest.java`

- [ ] **Step 1: Write the failing service test**

```java
// AggregatedInventoryServiceTest.java
package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregatedInventoryServiceTest {

    @Mock private EntityManager em;
    @Mock private Query nativeQuery;

    private AggregatedInventoryService service;

    @BeforeEach
    void setUp() {
        service = new AggregatedInventoryService(em);
    }

    @Test
    @DisplayName("search returns page response with mapped rows and total count")
    void search_returnsMappedPage() {
        Object[] row = new Object[] {
                1L, "75", "A_YYY", "Apple", "iPhone 3G",
                "IPHONE 3G 8GB A1241/A1324", "AT&T",
                0, new BigDecimal("0.0000"),
                7, new BigDecimal("2.0700")
        };

        when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(nativeQuery);
        when(nativeQuery.getResultList())
                .thenReturn(List.<Object[]>of(row))    // rows
                .thenReturn(List.of());                 // count fallthrough (not used here)
        when(nativeQuery.getSingleResult()).thenReturn(1L);

        AggregatedInventoryPageResponse resp = service.search(100L, null, null, null, null, null, null, 0, 20);

        assertThat(resp.content()).hasSize(1);
        assertThat(resp.content().get(0).ecoid2()).isEqualTo("75");
        assertThat(resp.content().get(0).brand()).isEqualTo("Apple");
        assertThat(resp.totalElements()).isEqualTo(1);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryServiceTest`
Expected: FAIL — `AggregatedInventoryService`, `AggregatedInventoryPageResponse`, `AggregatedInventoryRow` unresolved.

- [ ] **Step 3: Write the DTOs**

```java
// AggregatedInventoryRow.java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public record AggregatedInventoryRow(
        Long id,
        String ecoid2,
        String mergedGrade,
        String brand,
        String model,
        String name,
        String carrier,
        int dwTotalQuantity,
        BigDecimal dwAvgTargetPrice,
        int totalQuantity,
        BigDecimal avgTargetPrice
) {}
```

```java
// AggregatedInventoryPageResponse.java
package com.ecoatm.salesplatform.dto;

import java.util.List;

public record AggregatedInventoryPageResponse(
        List<AggregatedInventoryRow> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages
) {}
```

- [ ] **Step 4: Implement `AggregatedInventoryService.search()`**

Filter parameters map 1:1 to the QA grid filters. `productIdExact` is an exact-match integer on `ecoid2` (Mendix parity — the grid uses "Equal"). Grades/Brand/Model/ModelName/Carrier are case-insensitive contains.

```java
package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse;
import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AggregatedInventoryService {

    private final EntityManager em;

    public AggregatedInventoryService(EntityManager em) { this.em = em; }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static BigDecimal toBd(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal b) return b;
        return new BigDecimal(o.toString());
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public AggregatedInventoryPageResponse search(
            Long weekId,
            String productIdExact,
            String gradesContains,
            String brandContains,
            String modelContains,
            String modelNameContains,
            String carrierContains,
            int page,
            int pageSize) {

        String ecoid = blankToNull(productIdExact);
        String grades = blankToNull(gradesContains);
        String brand = blankToNull(brandContains);
        String model = blankToNull(modelContains);
        String name = blankToNull(modelNameContains);
        String carrier = blankToNull(carrierContains);

        int offset = page * pageSize;

        String where = """
                WHERE a.is_deprecated = false
                  AND (CAST(:weekId AS bigint) IS NULL OR a.week_id = CAST(:weekId AS bigint))
                  AND (CAST(:ecoid AS text)   IS NULL OR a.ecoid2 = CAST(:ecoid AS text))
                  AND (CAST(:grades AS text)  IS NULL OR LOWER(a.merged_grade)   LIKE LOWER(CONCAT('%', CAST(:grades AS text), '%')))
                  AND (CAST(:brand AS text)   IS NULL OR LOWER(a.brand)          LIKE LOWER(CONCAT('%', CAST(:brand AS text), '%')))
                  AND (CAST(:model AS text)   IS NULL OR LOWER(a.model)          LIKE LOWER(CONCAT('%', CAST(:model AS text), '%')))
                  AND (CAST(:name AS text)    IS NULL OR LOWER(a.name)           LIKE LOWER(CONCAT('%', CAST(:name AS text), '%')))
                  AND (CAST(:carrier AS text) IS NULL OR LOWER(a.carrier)        LIKE LOWER(CONCAT('%', CAST(:carrier AS text), '%')))
                """;

        String sql = """
                SELECT a.id, a.ecoid2, a.merged_grade, a.brand, a.model, a.name, a.carrier,
                       a.dw_total_quantity, a.dw_avg_target_price,
                       a.total_quantity, a.avg_target_price
                FROM auctions.aggregated_inventory a
                %s
                ORDER BY a.ecoid2 ASC, a.merged_grade ASC
                LIMIT :limit OFFSET :offset
                """.formatted(where);

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("weekId", weekId)
                .setParameter("ecoid", ecoid)
                .setParameter("grades", grades)
                .setParameter("brand", brand)
                .setParameter("model", model)
                .setParameter("name", name)
                .setParameter("carrier", carrier)
                .setParameter("limit", pageSize)
                .setParameter("offset", offset)
                .getResultList();

        String countSql = """
                SELECT COUNT(*) FROM auctions.aggregated_inventory a
                %s
                """.formatted(where);

        long total = ((Number) em.createNativeQuery(countSql)
                .setParameter("weekId", weekId)
                .setParameter("ecoid", ecoid)
                .setParameter("grades", grades)
                .setParameter("brand", brand)
                .setParameter("model", model)
                .setParameter("name", name)
                .setParameter("carrier", carrier)
                .getSingleResult()).longValue();

        List<AggregatedInventoryRow> content = rows.stream()
                .map(r -> new AggregatedInventoryRow(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        (String) r[4],
                        (String) r[5],
                        (String) r[6],
                        ((Number) r[7]).intValue(),
                        toBd(r[8]),
                        ((Number) r[9]).intValue(),
                        toBd(r[10])
                ))
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        return new AggregatedInventoryPageResponse(content, page, pageSize, total, totalPages);
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryServiceTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryRow.java \
        backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryPageResponse.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryServiceTest.java
git commit -m "feat: add paginated search to AggregatedInventoryService"
```

---

### Task 3.2: Totals endpoint

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryTotalsResponse.java`
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryService.java` (add `getTotals`)
- Modify: `AggregatedInventoryServiceTest.java`

- [ ] **Step 1: Append failing test**

```java
    @Test
    @DisplayName("getTotals computes averages from totals row + aggregated inventory")
    void getTotals_withTotalsRow_returnsKpis() {
        Object[] row = new Object[] {
                186020, new BigDecimal("1855306.00"), new BigDecimal("42.1700"),
                57298,  new BigDecimal("5269391.00"), new BigDecimal("214.5400"),
                java.sql.Timestamp.from(java.time.Instant.parse("2026-04-17T08:40:00Z"))
        };

        when(em.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(row);

        var totals = service.getTotals(100L);

        assertThat(totals.totalQuantity()).isEqualTo(186020);
        assertThat(totals.totalPayout()).isEqualByComparingTo(new BigDecimal("1855306.00"));
        assertThat(totals.dwAverageTargetPrice()).isEqualByComparingTo(new BigDecimal("214.5400"));
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryServiceTest`
Expected: FAIL — `getTotals` not defined.

- [ ] **Step 3: Write `AggregatedInventoryTotalsResponse`**

```java
// AggregatedInventoryTotalsResponse.java
package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AggregatedInventoryTotalsResponse(
        int totalQuantity,
        BigDecimal totalPayout,
        BigDecimal averageTargetPrice,
        int dwTotalQuantity,
        BigDecimal dwTotalPayout,
        BigDecimal dwAverageTargetPrice,
        Instant lastSyncedAt
) {}
```

- [ ] **Step 4: Implement `getTotals()`**

The totals row may or may not exist in `aggregated_inventory_totals`. If missing, compute on the fly — matches Mendix `DS_GetOrCreateAggregatedInventoryTotalsByWeek` which creates-if-absent. Average target price is a weighted average over `total_quantity * avg_target_price / sum(total_quantity)` to match the Mendix display.

```java
    @Transactional(readOnly = true)
    public com.ecoatm.salesplatform.dto.AggregatedInventoryTotalsResponse getTotals(Long weekId) {
        String sql = """
                SELECT
                  COALESCE(SUM(a.total_quantity), 0)                                           AS total_qty,
                  COALESCE(SUM(a.total_payout), 0)                                             AS total_payout,
                  CASE WHEN COALESCE(SUM(a.total_quantity), 0) = 0 THEN 0
                       ELSE SUM(a.avg_target_price * a.total_quantity) / SUM(a.total_quantity) END AS avg_target,
                  COALESCE(SUM(a.dw_total_quantity), 0)                                        AS dw_total_qty,
                  COALESCE(SUM(a.dw_total_payout), 0)                                          AS dw_total_payout,
                  CASE WHEN COALESCE(SUM(a.dw_total_quantity), 0) = 0 THEN 0
                       ELSE SUM(a.dw_avg_target_price * a.dw_total_quantity) / SUM(a.dw_total_quantity) END AS dw_avg_target,
                  MAX(a.changed_date)                                                          AS last_synced
                FROM auctions.aggregated_inventory a
                WHERE a.is_deprecated = false
                  AND (CAST(:weekId AS bigint) IS NULL OR a.week_id = CAST(:weekId AS bigint))
                """;

        Object[] r = (Object[]) em.createNativeQuery(sql)
                .setParameter("weekId", weekId)
                .getSingleResult();

        java.time.Instant lastSynced = null;
        if (r[6] instanceof java.sql.Timestamp ts) lastSynced = ts.toInstant();
        else if (r[6] instanceof java.time.Instant inst) lastSynced = inst;

        return new com.ecoatm.salesplatform.dto.AggregatedInventoryTotalsResponse(
                ((Number) r[0]).intValue(),
                toBd(r[1]),
                toBd(r[2]),
                ((Number) r[3]).intValue(),
                toBd(r[4]),
                toBd(r[5]),
                lastSynced
        );
    }
```

- [ ] **Step 5: Run test to verify it passes**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryServiceTest`
Expected: PASS (3 tests).

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryTotalsResponse.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryService.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryServiceTest.java
git commit -m "feat: add totals KPI endpoint for aggregated inventory"
```

---

### Task 3.3: Wire endpoints into `AggregatedInventoryController`

**Files:**
- Modify: `backend/src/main/java/com/ecoatm/salesplatform/controller/AggregatedInventoryController.java`
- Modify: `AggregatedInventoryControllerTest.java`

- [ ] **Step 1: Write failing controller tests**

```java
    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET / returns paginated response")
    void list_returnsPage() throws Exception {
        var row = new com.ecoatm.salesplatform.dto.AggregatedInventoryRow(
                1L, "75", "A_YYY", "Apple", "iPhone 3G",
                "IPHONE 3G 8GB A1241/A1324", "AT&T",
                0, java.math.BigDecimal.ZERO, 7, new java.math.BigDecimal("2.0700"));
        when(service.search(100L, null, null, null, null, null, null, 0, 20))
                .thenReturn(new com.ecoatm.salesplatform.dto.AggregatedInventoryPageResponse(
                        java.util.List.of(row), 0, 20, 1L, 1));

        mvc.perform(get("/api/v1/admin/inventory?weekId=100&page=0&pageSize=20"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].ecoid2").value("75"))
           .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("GET /totals returns KPI payload")
    void totals_returnsKpis() throws Exception {
        when(service.getTotals(100L)).thenReturn(new com.ecoatm.salesplatform.dto.AggregatedInventoryTotalsResponse(
                186020, new java.math.BigDecimal("1855306.00"), new java.math.BigDecimal("42.1700"),
                57298,  new java.math.BigDecimal("5269391.00"), new java.math.BigDecimal("214.5400"),
                java.time.Instant.parse("2026-04-17T08:40:00Z")));

        mvc.perform(get("/api/v1/admin/inventory/totals?weekId=100"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalQuantity").value(186020))
           .andExpect(jsonPath("$.dwAverageTargetPrice").value(214.54));
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryControllerTest`
Expected: FAIL — controller has no `list` / `totals` handlers yet.

- [ ] **Step 3: Add handlers to controller**

```java
    @GetMapping
    public ResponseEntity<AggregatedInventoryPageResponse> list(
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String grades,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String carrier,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.search(weekId, productId, grades, brand, model, modelName, carrier, page, pageSize));
    }

    @GetMapping("/totals")
    public ResponseEntity<AggregatedInventoryTotalsResponse> totals(@RequestParam(required = false) Long weekId) {
        return ResponseEntity.ok(service.getTotals(weekId));
    }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryControllerTest`
Expected: PASS (3 tests).

- [ ] **Step 5: Commit**

```bash
git commit -am "feat: wire inventory list and totals endpoints"
```

---

## Phase 4 — Frontend grid, filters, KPI strip, pagination

### Task 4.1: Fetch helpers for rows and totals

**Files:**
- Modify: `frontend/src/lib/aggregatedInventory.ts`

- [ ] **Step 1: Add fetch helpers**

```ts
// appended to frontend/src/lib/aggregatedInventory.ts

export interface InventorySearchParams {
  weekId: number;
  productId?: string;
  grades?: string;
  brand?: string;
  model?: string;
  modelName?: string;
  carrier?: string;
  page: number;
  pageSize: number;
}

export async function fetchInventoryPage(p: InventorySearchParams): Promise<InventoryPageResponse> {
  const qs = new URLSearchParams();
  qs.set('weekId', String(p.weekId));
  qs.set('page', String(p.page));
  qs.set('pageSize', String(p.pageSize));
  if (p.productId)  qs.set('productId', p.productId);
  if (p.grades)     qs.set('grades', p.grades);
  if (p.brand)      qs.set('brand', p.brand);
  if (p.model)      qs.set('model', p.model);
  if (p.modelName)  qs.set('modelName', p.modelName);
  if (p.carrier)    qs.set('carrier', p.carrier);

  const res = await apiFetch(`/api/v1/admin/inventory?${qs}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return InventoryPageResponseSchema.parse(await res.json());
}

export async function fetchInventoryTotals(weekId: number): Promise<InventoryTotals> {
  const res = await apiFetch(`/api/v1/admin/inventory/totals?weekId=${weekId}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return InventoryTotalsSchema.parse(await res.json());
}
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/lib/aggregatedInventory.ts
git commit -m "feat: add inventory search and totals fetch helpers"
```

---

### Task 4.2: KPI strip + grid + filters + pagination

**Files:**
- Modify: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx`

- [ ] **Step 1: Rewrite `page.tsx` with filters, grid, pagination**

```tsx
'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import styles from './inventory.module.css';
import {
  fetchWeeks, fetchInventoryPage, fetchInventoryTotals,
  type WeekOption, type InventoryPageResponse, type InventoryTotals,
} from '@/lib/aggregatedInventory';

const PAGE_SIZE = 20;
const FILTER_DELAY = 500;

interface Filters {
  productId: string;
  grades: string;
  brand: string;
  model: string;
  modelName: string;
  carrier: string;
}

const EMPTY_FILTERS: Filters = {
  productId: '', grades: '', brand: '', model: '', modelName: '', carrier: '',
};

const formatUsd = (n: number) =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 2 }).format(n);
const formatInt = (n: number) =>
  new Intl.NumberFormat('en-US').format(n);

export default function AggregatedInventoryPage() {
  const [weeks, setWeeks] = useState<WeekOption[]>([]);
  const [weekId, setWeekId] = useState<number | null>(null);
  const [page, setPage] = useState(0);
  const [data, setData] = useState<InventoryPageResponse | null>(null);
  const [totals, setTotals] = useState<InventoryTotals | null>(null);

  const [input, setInput] = useState<Filters>(EMPTY_FILTERS);
  const [applied, setApplied] = useState<Filters>(EMPTY_FILTERS);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Load weeks once
  useEffect(() => {
    fetchWeeks().then(list => {
      setWeeks(list);
      const now = Date.now();
      const current = list.find(w => new Date(w.weekEndDateTime).getTime() > now);
      setWeekId((current ?? list[0])?.id ?? null);
    }).catch(() => setWeeks([]));
  }, []);

  // Debounce filter input
  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setApplied(input);
      setPage(0);
    }, FILTER_DELAY);
    return () => { if (debounceRef.current) clearTimeout(debounceRef.current); };
  }, [input]);

  const refresh = useCallback(async () => {
    if (!weekId) return;
    const [grid, kpi] = await Promise.all([
      fetchInventoryPage({
        weekId, page, pageSize: PAGE_SIZE,
        productId: applied.productId || undefined,
        grades:    applied.grades    || undefined,
        brand:     applied.brand     || undefined,
        model:     applied.model     || undefined,
        modelName: applied.modelName || undefined,
        carrier:   applied.carrier   || undefined,
      }),
      fetchInventoryTotals(weekId),
    ]);
    setData(grid);
    setTotals(kpi);
  }, [weekId, page, applied]);

  useEffect(() => { refresh().catch(() => {/* network logged upstream */}); }, [refresh]);

  const updateFilter = <K extends keyof Filters>(k: K, v: string) =>
    setInput(prev => ({ ...prev, [k]: v }));

  const total = data?.totalElements ?? 0;
  const startIdx = total === 0 ? 0 : page * PAGE_SIZE + 1;
  const endIdx = Math.min(total, (page + 1) * PAGE_SIZE);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h2 className={styles.title}>Inventory</h2>
        <select className={styles.weekSelect} value={weekId ?? ''}
                onChange={e => { setWeekId(Number(e.target.value)); setPage(0); }}>
          {weeks.map(w => <option key={w.id} value={w.id}>{w.weekDisplay}</option>)}
        </select>
        <button className={styles.button} type="button">Create Auction</button>
        <button className={styles.buttonGhost} type="button" onClick={() => refresh()}>Refresh</button>
        <span className={styles.lastSynced}>
          Last synced: {totals?.lastSyncedAt ? new Date(totals.lastSyncedAt).toLocaleString() : '—'}
        </span>
      </header>

      <section className={styles.kpiStrip} aria-label="Inventory totals">
        <Kpi label="Total Quantity"            value={formatInt(totals?.totalQuantity ?? 0)} />
        <Kpi label="Total Payout"              value={formatUsd(Number(totals?.totalPayout ?? 0))} />
        <Kpi label="Average Target Price"      value={formatUsd(Number(totals?.averageTargetPrice ?? 0))} />
        <Kpi label="DW Total Quantity"         value={formatInt(totals?.dwTotalQuantity ?? 0)} />
        <Kpi label="DW Total Payout"           value={formatUsd(Number(totals?.dwTotalPayout ?? 0))} />
        <Kpi label="DW Average Target Price"   value={formatUsd(Number(totals?.dwAverageTargetPrice ?? 0))} />
      </section>

      <div className={styles.gridWrap}>
        <table className={styles.grid}>
          <thead>
            <tr>
              <HeaderCell label="Product ID"       filter={input.productId} onChange={v => updateFilter('productId', v)} kind="equal" />
              <HeaderCell label="Grades"           filter={input.grades}    onChange={v => updateFilter('grades', v)} />
              <HeaderCell label="Brand"            filter={input.brand}     onChange={v => updateFilter('brand', v)} />
              <HeaderCell label="Model"            filter={input.model}     onChange={v => updateFilter('model', v)} />
              <HeaderCell label="Model Name"       filter={input.modelName} onChange={v => updateFilter('modelName', v)} />
              <HeaderCell label="Carrier"          filter={input.carrier}   onChange={v => updateFilter('carrier', v)} />
              <th>DW Qty</th>
              <th>DW Target Price</th>
              <th>Total Qty</th>
              <th>Target Price</th>
              <th aria-label="actions" />
            </tr>
          </thead>
          <tbody>
            {(data?.content ?? []).map(r => (
              <tr key={r.id}>
                <td>{r.ecoid2}</td>
                <td>{r.mergedGrade}</td>
                <td>{r.brand}</td>
                <td>{r.model}</td>
                <td>{r.name}</td>
                <td>{r.carrier}</td>
                <td>{formatInt(r.dwTotalQuantity)}</td>
                <td>{formatUsd(Number(r.dwAvgTargetPrice))}</td>
                <td>{formatInt(r.totalQuantity)}</td>
                <td>{formatUsd(Number(r.avgTargetPrice))}</td>
                <td />
              </tr>
            ))}
          </tbody>
        </table>

        <div className={styles.pagination}>
          <button type="button" onClick={() => setPage(0)} disabled={page === 0}>«</button>
          <button type="button" onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>‹</button>
          <span>Currently showing {startIdx} to {endIdx} of {total}</span>
          <button type="button" onClick={() => setPage(p => p + 1)}
                  disabled={data ? page + 1 >= data.totalPages : true}>›</button>
          <button type="button" onClick={() => data && setPage(data.totalPages - 1)}
                  disabled={data ? page + 1 >= data.totalPages : true}>»</button>
        </div>
      </div>
    </div>
  );
}

function Kpi({ label, value }: { label: string; value: string }) {
  return (
    <div className={styles.kpiCard}>
      <p className={styles.kpiLabel}>{label}</p>
      <p className={styles.kpiValue}>{value}</p>
    </div>
  );
}

interface HeaderCellProps {
  label: string;
  filter: string;
  onChange: (v: string) => void;
  kind?: 'contains' | 'equal';
}
function HeaderCell({ label, filter, onChange, kind = 'contains' }: HeaderCellProps) {
  return (
    <th>
      <div>{label}</div>
      <input
        className={styles.filterInput}
        value={filter}
        placeholder={kind === 'equal' ? '=' : 'Ab'}
        onChange={e => onChange(e.target.value)}
        inputMode={kind === 'equal' ? 'numeric' : 'text'}
      />
    </th>
  );
}
```

- [ ] **Step 2: Start dev server and visually verify against QA**

Run: `cd frontend && npm run dev`, then open both pages side-by-side:
- Local: `http://localhost:3000/admin/auctions-data-center/inventory`
- QA: `https://buy-qa.ecoatmdirect.com/index.html` → Admin → Inventory

Check:
- Column order matches exactly: Product ID, Grades, Brand, Model, Model Name, Carrier, DW Qty, DW Target Price, Total Qty, Target Price.
- KPI strip order matches: Total Quantity, Total Payout, Average Target Price, DW Total Quantity, DW Total Payout, DW Average Target Price.
- Pagination text says `Currently showing 1 to 20 of N`.
- Teal header buttons (#407874) match.
- Filter inputs are 24px tall with `Ab` / `=` placeholders.

Fix any discrepancies before continuing.

- [ ] **Step 3: Commit**

```bash
git commit -am "feat: render inventory grid with filters, KPIs, pagination"
```

---

## Phase 5 — Edit popup

The Mendix popup `AggregatedInventory_NewEdit` is admin-only. Captured fields per `migration_context`:
- `MergedGrade` — 3-way radio (`A_YYY`, `C_YNY/G_YNN`, `E_YYN` — read from enum). Frontend treats as free text for simplicity, but constrain to the observed values (from the grid `Grades` column).
- `Datawipe` — boolean radio (`Yes` / `No`).
- `TotalQuantity` / `DwTotalQuantity` — editable integers (Mendix `isTotalQuantityModified` flag flips on save).

Save flow ends with `ACT_AggregateInventory_UpdateByAdmin` → Commit. No cascading recompute: the next `sync` run overwrites uninteresting fields anyway; the `isTotalQuantityModified` flag tells the sync job to preserve the admin's quantity override.

### Task 5.1: `PUT /api/v1/admin/inventory/{id}` backend

**Files:**
- Create: `backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryUpdateRequest.java`
- Modify: `AggregatedInventoryService.java` (add `updateRow`)
- Modify: `AggregatedInventoryController.java` (add handler)
- Modify: both service and controller tests.

- [ ] **Step 1: Write the failing service test**

```java
    @Test
    @DisplayName("updateRow saves mergedGrade, datawipe, totals and flips isTotalQuantityModified")
    void updateRow_persistsAdminEdit() {
        var entity = new com.ecoatm.salesplatform.model.auctions.AggregatedInventory();
        entity.setTotalQuantity(5);
        entity.setDwTotalQuantity(2);
        entity.setMergedGrade("A_YYY");
        when(em.find(com.ecoatm.salesplatform.model.auctions.AggregatedInventory.class, 99L))
                .thenReturn(entity);

        var req = new com.ecoatm.salesplatform.dto.AggregatedInventoryUpdateRequest(
                "E_YYN", true, 9, 4);

        var updated = service.updateRow(99L, req);

        assertThat(updated.getMergedGrade()).isEqualTo("E_YYN");
        assertThat(updated.isDatawipe()).isTrue();
        assertThat(updated.getTotalQuantity()).isEqualTo(9);
        assertThat(updated.getDwTotalQuantity()).isEqualTo(4);
        assertThat(updated.isTotalQuantityModified()).isTrue();
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryServiceTest`
Expected: FAIL — `updateRow` / `AggregatedInventoryUpdateRequest` unresolved.

- [ ] **Step 3: Write DTO**

```java
// AggregatedInventoryUpdateRequest.java
package com.ecoatm.salesplatform.dto;

public record AggregatedInventoryUpdateRequest(
        String mergedGrade,
        boolean datawipe,
        int totalQuantity,
        int dwTotalQuantity
) {}
```

- [ ] **Step 4: Implement `updateRow`** (change service constructor to also accept `EntityManager` which it already does — add a `find` + mutate + persist)

```java
    @Transactional
    public com.ecoatm.salesplatform.model.auctions.AggregatedInventory updateRow(
            Long id,
            com.ecoatm.salesplatform.dto.AggregatedInventoryUpdateRequest req) {
        var entity = em.find(com.ecoatm.salesplatform.model.auctions.AggregatedInventory.class, id);
        if (entity == null) {
            throw new jakarta.persistence.EntityNotFoundException("AggregatedInventory not found: " + id);
        }
        entity.setMergedGrade(req.mergedGrade());
        entity.setDatawipe(req.datawipe());
        entity.setTotalQuantity(req.totalQuantity());
        entity.setDwTotalQuantity(req.dwTotalQuantity());
        entity.setTotalQuantityModified(true);
        entity.setChangedDate(java.time.Instant.now());
        return em.merge(entity);
    }
```

- [ ] **Step 5: Add controller handler + test**

Controller:
```java
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<com.ecoatm.salesplatform.dto.AggregatedInventoryRow> update(
            @PathVariable Long id,
            @RequestBody com.ecoatm.salesplatform.dto.AggregatedInventoryUpdateRequest req) {
        var e = service.updateRow(id, req);
        var row = new com.ecoatm.salesplatform.dto.AggregatedInventoryRow(
                e.getId(), e.getEcoid2(), e.getMergedGrade(),
                e.getBrand(), e.getModel(), e.getName(), e.getCarrier(),
                e.getDwTotalQuantity(), e.getDwAvgTargetPrice(),
                e.getTotalQuantity(), e.getAvgTargetPrice());
        return ResponseEntity.ok(row);
    }
```

Test:
```java
    @Test
    @WithMockUser(roles = {"Administrator"})
    @DisplayName("PUT /{id} saves admin edit")
    void update_savesEdit() throws Exception {
        var entity = new com.ecoatm.salesplatform.model.auctions.AggregatedInventory();
        entity.setEcoid2("75");
        entity.setMergedGrade("E_YYN");
        entity.setDatawipe(true);
        entity.setTotalQuantity(9);
        entity.setDwTotalQuantity(4);
        when(service.updateRow(org.mockito.ArgumentMatchers.eq(42L),
                               org.mockito.ArgumentMatchers.any())).thenReturn(entity);

        mvc.perform(put("/api/v1/admin/inventory/42")
                .contentType("application/json")
                .content("{\"mergedGrade\":\"E_YYN\",\"datawipe\":true,\"totalQuantity\":9,\"dwTotalQuantity\":4}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.mergedGrade").value("E_YYN"))
           .andExpect(jsonPath("$.totalQuantity").value(9));
    }
```

- [ ] **Step 6: Run both tests to verify they pass**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryServiceTest,AggregatedInventoryControllerTest`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/dto/AggregatedInventoryUpdateRequest.java \
        backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryService.java \
        backend/src/main/java/com/ecoatm/salesplatform/controller/AggregatedInventoryController.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryServiceTest.java \
        backend/src/test/java/com/ecoatm/salesplatform/controller/AggregatedInventoryControllerTest.java
git commit -m "feat: add admin edit endpoint for aggregated inventory row"
```

---

### Task 5.2: Frontend edit modal

**Files:**
- Modify: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx` (add modal state + button)
- Modify: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/inventory.module.css` (add modal styles)
- Modify: `frontend/src/lib/aggregatedInventory.ts` (add `updateInventoryRow`)

- [ ] **Step 1: Append fetch helper**

```ts
export async function updateInventoryRow(
  id: number,
  body: { mergedGrade: string; datawipe: boolean; totalQuantity: number; dwTotalQuantity: number; }
): Promise<InventoryRow> {
  const res = await apiFetch(`/api/v1/admin/inventory/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return InventoryRowSchema.parse(await res.json());
}
```

- [ ] **Step 2: Append modal CSS**

```css
.modalBackdrop {
  position: fixed; inset: 0;
  background: rgba(17, 45, 50, 0.45);
  display: flex; align-items: center; justify-content: center;
  z-index: 100;
}
.modal {
  background: #fff; border-radius: 6px;
  width: min(420px, 92vw);
  padding: 1.25rem 1.5rem;
  box-shadow: 0 20px 60px rgba(0,0,0,.2);
}
.modalTitle {
  margin: 0 0 1rem; font-size: 1.15rem; color: #112d32;
}
.field { display: flex; flex-direction: column; gap: .25rem; margin-bottom: .75rem; }
.field label { font-size: .8rem; color: #555; }
.field input, .field select {
  height: 34px; padding: 0 .5rem;
  border: 1px solid #cfcfcf; border-radius: 4px; font: inherit;
}
.radioGroup { display: flex; gap: 1rem; }
.modalActions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: 1rem; }
.editLink {
  color: #407874; background: none; border: 0; cursor: pointer;
  text-decoration: underline; padding: 0; font: inherit;
}
```

- [ ] **Step 3: Add the Edit button in the trailing cell and a modal state in `page.tsx`**

Replace the placeholder `<td />` in the row with an Edit button and add `<EditModal>` below the pagination.

```tsx
// At the top of the component
const [editRow, setEditRow] = useState<InventoryRow | null>(null);

// In the row map (replace trailing <td />):
<td>
  <button type="button" className={styles.editLink} onClick={() => setEditRow(r)}>Edit</button>
</td>

// After </div> gridWrap, before </div> page:
{editRow && (
  <EditModal
    row={editRow}
    onClose={() => setEditRow(null)}
    onSaved={() => { setEditRow(null); refresh(); }}
  />
)}
```

Add `EditModal` component in the same file:

```tsx
function EditModal({ row, onClose, onSaved }: { row: InventoryRow; onClose: () => void; onSaved: () => void }) {
  const [mergedGrade, setMergedGrade]       = useState(row.mergedGrade ?? '');
  const [datawipe, setDatawipe]             = useState(false);
  const [totalQuantity, setTotalQuantity]   = useState(row.totalQuantity);
  const [dwTotalQuantity, setDwTotalQuantity] = useState(row.dwTotalQuantity);
  const [saving, setSaving] = useState(false);

  const onSave = async () => {
    setSaving(true);
    try {
      await updateInventoryRow(row.id, { mergedGrade, datawipe, totalQuantity, dwTotalQuantity });
      onSaved();
    } finally { setSaving(false); }
  };

  return (
    <div className={styles.modalBackdrop} role="dialog" aria-modal="true">
      <div className={styles.modal}>
        <h3 className={styles.modalTitle}>Edit Aggregated Inventory</h3>
        <div className={styles.field}>
          <label>Merged Grade</label>
          <div className={styles.radioGroup}>
            {['A_YYY', 'C_YNY/G_YNN', 'E_YYN'].map(g => (
              <label key={g}>
                <input type="radio" name="grade" value={g}
                       checked={mergedGrade === g}
                       onChange={() => setMergedGrade(g)} />
                {' '}{g}
              </label>
            ))}
          </div>
        </div>
        <div className={styles.field}>
          <label>Data Wipe</label>
          <div className={styles.radioGroup}>
            <label><input type="radio" name="dw" checked={datawipe} onChange={() => setDatawipe(true)} /> Yes</label>
            <label><input type="radio" name="dw" checked={!datawipe} onChange={() => setDatawipe(false)} /> No</label>
          </div>
        </div>
        <div className={styles.field}>
          <label>Total Quantity</label>
          <input type="number" value={totalQuantity} onChange={e => setTotalQuantity(Number(e.target.value))} />
        </div>
        <div className={styles.field}>
          <label>DW Total Quantity</label>
          <input type="number" value={dwTotalQuantity} onChange={e => setDwTotalQuantity(Number(e.target.value))} />
        </div>
        <div className={styles.modalActions}>
          <button type="button" className={styles.buttonGhost} onClick={onClose} disabled={saving}>Cancel</button>
          <button type="button" className={styles.button} onClick={onSave} disabled={saving}>
            {saving ? 'Saving…' : 'Save'}
          </button>
        </div>
      </div>
    </div>
  );
}
```

Also add the `updateInventoryRow` import:
```tsx
import {
  fetchWeeks, fetchInventoryPage, fetchInventoryTotals, updateInventoryRow,
  type WeekOption, type InventoryPageResponse, type InventoryTotals, type InventoryRow,
} from '@/lib/aggregatedInventory';
```

- [ ] **Step 4: Start dev server and verify**

Run: `cd frontend && npm run dev`. Click Edit on any row, change a quantity, Save. Grid refreshes. Ensure the KPI strip reflects the change after refresh.

- [ ] **Step 5: Commit**

```bash
git commit -am "feat: add admin edit modal for aggregated inventory"
```

---

## Phase 6 — Excel export

### Task 6.1: Backend exporter (Apache POI)

**Files:**
- Verify `poi-ooxml` is in `backend/pom.xml` — if missing, add it (see step 0).
- Create: `backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryExcelExporter.java`
- Modify: `AggregatedInventoryController.java` (add `/export` handler)
- Test: `backend/src/test/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryExcelExporterTest.java`

- [ ] **Step 0: Ensure dependency exists**

```bash
grep -R "poi-ooxml" backend/pom.xml || echo "MISSING: add dependency below"
```

If missing, add to `backend/pom.xml`:

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

- [ ] **Step 1: Write the failing exporter test**

```java
// AggregatedInventoryExcelExporterTest.java
package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AggregatedInventoryExcelExporterTest {

    @Test
    @DisplayName("writeWorkbook produces a workbook with header row and data rows")
    void writeWorkbook_hasHeaderAndData() throws Exception {
        var rows = List.of(new AggregatedInventoryRow(
                1L, "75", "A_YYY", "Apple", "iPhone 3G",
                "IPHONE 3G 8GB A1241/A1324", "AT&T",
                0, BigDecimal.ZERO, 7, new BigDecimal("2.07")));

        var out = new ByteArrayOutputStream();
        new AggregatedInventoryExcelExporter().write(rows, out);

        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(out.toByteArray()))) {
            var sheet = wb.getSheetAt(0);
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Product ID");
            assertThat(sheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Brand");
            assertThat(sheet.getRow(1).getCell(0).getStringCellValue()).isEqualTo("75");
            assertThat(sheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("iPhone 3G");
            assertThat(sheet.getRow(1).getCell(8).getNumericCellValue()).isEqualTo(7);
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryExcelExporterTest`
Expected: FAIL — class unresolved.

- [ ] **Step 3: Write exporter**

```java
// AggregatedInventoryExcelExporter.java
package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.dto.AggregatedInventoryRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
public class AggregatedInventoryExcelExporter {

    private static final String[] HEADERS = {
            "Product ID", "Grades", "Brand", "Model", "Model Name", "Carrier",
            "DW Qty", "DW Target Price", "Total Qty", "Target Price"
    };

    public void write(List<AggregatedInventoryRow> rows, OutputStream out) throws java.io.IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Aggregated Inventory");

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) header.createCell(i).setCellValue(HEADERS[i]);

            int r = 1;
            for (var row : rows) {
                Row xr = sheet.createRow(r++);
                xr.createCell(0).setCellValue(row.ecoid2());
                xr.createCell(1).setCellValue(nullToEmpty(row.mergedGrade()));
                xr.createCell(2).setCellValue(nullToEmpty(row.brand()));
                xr.createCell(3).setCellValue(nullToEmpty(row.model()));
                xr.createCell(4).setCellValue(nullToEmpty(row.name()));
                xr.createCell(5).setCellValue(nullToEmpty(row.carrier()));
                xr.createCell(6).setCellValue(row.dwTotalQuantity());
                xr.createCell(7).setCellValue(bdToDouble(row.dwAvgTargetPrice()));
                xr.createCell(8).setCellValue(row.totalQuantity());
                xr.createCell(9).setCellValue(bdToDouble(row.avgTargetPrice()));
            }
            wb.write(out);
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static double bdToDouble(BigDecimal b) { return b == null ? 0 : b.doubleValue(); }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -pl backend test -Dtest=AggregatedInventoryExcelExporterTest`
Expected: PASS.

- [ ] **Step 5: Add `/export` controller handler**

In `AggregatedInventoryController`, add the exporter via constructor and a streaming endpoint:

```java
    // add field + constructor param
    private final AggregatedInventoryExcelExporter excelExporter;

    // …update constructor signature to accept it…

    @GetMapping(value = "/export", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) Long weekId,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String grades,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String carrier) throws java.io.IOException {

        var all = service.search(weekId, productId, grades, brand, model, modelName, carrier, 0, Integer.MAX_VALUE);
        var baos = new java.io.ByteArrayOutputStream();
        excelExporter.write(all.content(), baos);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"aggregated-inventory.xlsx\"")
                .body(baos.toByteArray());
    }
```

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryExcelExporter.java \
        backend/src/main/java/com/ecoatm/salesplatform/controller/AggregatedInventoryController.java \
        backend/src/test/java/com/ecoatm/salesplatform/service/auctions/AggregatedInventoryExcelExporterTest.java \
        backend/pom.xml
git commit -m "feat: add Excel export for aggregated inventory"
```

---

### Task 6.2: Frontend Download button

**Files:**
- Modify: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx`

- [ ] **Step 1: Add Download button at the bottom of gridWrap (Mendix parity — bottom of page)**

```tsx
// In page.tsx, after </div> of pagination and before closing gridWrap
<div style={{ padding: '.75rem 1rem', borderTop: '1px solid #e5e5e5' }}>
  <button
    type="button"
    className={styles.button}
    onClick={() => {
      if (!weekId) return;
      const qs = new URLSearchParams({ weekId: String(weekId) });
      if (applied.productId) qs.set('productId', applied.productId);
      if (applied.grades)    qs.set('grades', applied.grades);
      if (applied.brand)     qs.set('brand', applied.brand);
      if (applied.model)     qs.set('model', applied.model);
      if (applied.modelName) qs.set('modelName', applied.modelName);
      if (applied.carrier)   qs.set('carrier', applied.carrier);
      window.location.href = `/api/v1/admin/inventory/export?${qs}`;
    }}
  >
    Download
  </button>
</div>
```

- [ ] **Step 2: Verify download in browser**

Run: `cd frontend && npm run dev`. Click Download. Confirm `aggregated-inventory.xlsx` downloads and opens in Excel with header row + matching data.

- [ ] **Step 3: Commit**

```bash
git commit -am "feat: wire Excel download button on inventory page"
```

---

## Phase 7 — Create Auction stub + refresh

### Task 7.1: Create Auction stub

The Mendix `ACT_PreCreateAuction` microflow is a full auction-scheduling flow. That module is not ported yet (stub at `frontend/src/app/(dashboard)/admin/auctions-data-center/page.tsx`). Phase 1 ships a stub that toasts "Auction scheduling is coming soon" to prevent dead-button regression.

**Files:**
- Modify: `frontend/src/app/(dashboard)/admin/auctions-data-center/inventory/page.tsx`

- [ ] **Step 1: Wire an alert placeholder**

Replace the `<button className={styles.button} type="button">Create Auction</button>` with:

```tsx
<button
  className={styles.button}
  type="button"
  onClick={() => alert('Auction scheduling is being ported — see docs/tasks/auctions-schema-migration-plan.md')}
>
  Create Auction
</button>
```

Refresh button is already wired via `onClick={() => refresh()}`.

- [ ] **Step 2: Commit**

```bash
git commit -am "feat: add Create Auction placeholder until auction module ports"
```

---

## Phase 8 — Docs, ADR, E2E test, pixel-parity sweep

### Task 8.1: REST endpoints doc

**Files:**
- Modify: `docs/api/rest-endpoints.md`

- [ ] **Step 1: Append new section**

Add the following section above `## Auth` (near the end of the file):

```markdown
---

## Aggregated Inventory (Admin)

Backs `/admin/auctions-data-center/inventory`. Mirrors Mendix `AuctionUI.PG_AggregatedInventory`.

### GET /admin/inventory/weeks

Return all weeks ordered by start datetime descending. Used to populate the week selector.

**Response**: `WeekOption[]` — `{ id, weekDisplay, weekStartDateTime, weekEndDateTime }`.

### GET /admin/inventory

Paginated rows for the selected week. Requires `Administrator` or `SalesOps`.

| Param | Type | Default | Description |
|---|---|---|---|
| weekId | long | - | FK into `mdm.week` |
| productId | string | - | Exact match on `ecoid2` |
| grades | string | - | Contains match on `merged_grade` |
| brand | string | - | Contains on `brand` |
| model | string | - | Contains on `model` |
| modelName | string | - | Contains on `name` |
| carrier | string | - | Contains on `carrier` |
| page | int | 0 | Page number |
| pageSize | int | 20 | Page size |

**Response**: `AggregatedInventoryPageResponse` — `{ content: AggregatedInventoryRow[], page, pageSize, totalElements, totalPages }`.

### GET /admin/inventory/totals

Per-week KPI totals (sum + weighted average). Rows with `is_deprecated = true` are excluded.

**Response**: `{ totalQuantity, totalPayout, averageTargetPrice, dwTotalQuantity, dwTotalPayout, dwAverageTargetPrice, lastSyncedAt }`.

### PUT /admin/inventory/{id}

Admin row edit. Requires `Administrator`. Flips `is_total_quantity_modified = true` on save so subsequent sync runs preserve the override.

**Request body**: `{ mergedGrade, datawipe, totalQuantity, dwTotalQuantity }`.

**Response**: `AggregatedInventoryRow`.

### GET /admin/inventory/export

Streams an `.xlsx` of the current filter set (same query params as list). Not paginated.

**Response**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` (binary).
```

- [ ] **Step 2: Commit**

```bash
git add docs/api/rest-endpoints.md
git commit -m "docs: document aggregated inventory REST endpoints"
```

---

### Task 8.2: ADR entry

**Files:**
- Modify: `docs/architecture/decisions.md`

- [ ] **Step 1: Prepend ADR (new entries go on top)**

Insert immediately after the header comment, before the first `---` divider:

```markdown
---

## 2026-04-17 — Aggregated Inventory: compute totals at read time + keep quantity override flag

**Status:** Accepted (Phases 3.2 and 5.1 of `docs/tasks/aggregated-inventory-page-plan.md`).

### Context

Mendix `AggregatedInventoryTotals` stored one row per week with precomputed
sums. Mendix also exposed `isTotalQuantityModified` on `AggregatedInventory`
so a nightly sync could tell whether to overwrite an admin-edited quantity.
Porting 1:1 would duplicate state and require a scheduled refresher. The
legacy bug surface is the same one the buyer-overview ADR (2026-04-15) fixed.

### Decision

- **Totals computed at read time** from `auctions.aggregated_inventory`
  with weighted averages (`SUM(price * qty) / SUM(qty)`) rather than
  reading `auctions.aggregated_inventory_totals`. The totals table stays
  in the schema for future Snowflake export parity but is not read by the
  page.
- **Preserve `is_total_quantity_modified`** exactly as the legacy column.
  The PUT handler flips it to `true` on save, and the (future) inventory
  sync must honor the flag.
- **Excluded rows:** `is_deprecated = true` rows never appear in the grid
  or the KPI strip.

### Alternatives considered

- **Read `aggregated_inventory_totals` directly.** Rejected for the same
  reason as the buyer overview: the denormalized row drifts whenever an
  admin edit lands out-of-band. Recomputing is O(N) over ~87k rows
  filtered by `week_id` — sub-second with the `idx_agi_week` index.
- **Materialized view.** Over-engineered for 87k rows. Revisit if
  `EXPLAIN ANALYZE` regresses past 200 ms.

### Consequences

- The totals table is now informational only. A follow-up can either
  drop it or wire the Snowflake sync to populate it from the same query.
- Create Auction remains a stub until the auction scheduling module
  lands — the page's button shows a placeholder dialog rather than a
  404 or a dead action.

### References

- Plan: `docs/tasks/aggregated-inventory-page-plan.md`
- Schema: `backend/src/main/resources/db/migration/V60__auctions_aggregated_inventory.sql`
- Mendix source: `migration_context/frontend/components/Pages_Page/PG_AggregatedInventory.md`

---
```

- [ ] **Step 2: Commit**

```bash
git add docs/architecture/decisions.md
git commit -m "docs: ADR for aggregated inventory computation strategy"
```

---

### Task 8.3: Playwright E2E

**Files:**
- Create: `frontend/tests/e2e/aggregated-inventory.spec.ts`

- [ ] **Step 1: Write E2E**

```ts
import { test, expect } from '@playwright/test';

test.describe('Aggregated Inventory', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.getByLabel('Email').fill('admin@test.com');
    await page.getByLabel('Password').fill('Admin123!');
    await page.getByRole('button', { name: /sign in/i }).click();
    await page.goto('/admin/auctions-data-center/inventory');
  });

  test('shows KPI strip, grid, and paginates', async ({ page }) => {
    await expect(page.getByRole('heading', { name: 'Inventory' })).toBeVisible();
    await expect(page.getByText('Total Quantity')).toBeVisible();
    await expect(page.getByText('DW Average Target Price')).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Product ID/ })).toBeVisible();
    await expect(page.getByText(/Currently showing \d+ to \d+ of/)).toBeVisible();
  });

  test('filters by grade (contains)', async ({ page }) => {
    await page.getByPlaceholder('Ab').first().fill('A_YYY');
    await expect(page.getByText(/Currently showing/)).toBeVisible({ timeout: 5000 });
  });
});
```

- [ ] **Step 2: Run it**

```bash
cd frontend && npx playwright test tests/e2e/aggregated-inventory.spec.ts
```

- [ ] **Step 3: Commit**

```bash
git add frontend/tests/e2e/aggregated-inventory.spec.ts
git commit -m "test: add E2E for aggregated inventory page"
```

---

### Task 8.4: Pixel-parity sweep

- [ ] **Step 1: Playwright screenshot both QA and local at 1440×900**

```bash
# From the repo root
npx playwright screenshot --viewport-size=1440,900 \
  --full-page https://buy-qa.ecoatmdirect.com/index.html qa-inventory.png
npx playwright screenshot --viewport-size=1440,900 \
  --full-page http://localhost:3000/admin/auctions-data-center/inventory local-inventory.png
```

- [ ] **Step 2: Manual diff checklist**

Open both screenshots side-by-side and verify against CLAUDE.md §Styling QA Verification:
1. Sidebar nav shape/width (not covered by this page — parent layout).
2. Header title size + "Inventory" label.
3. Week selector dropdown — `"2026 / Wk17"` value rendered.
4. Teal buttons (#407874), 34px height, same corner radius.
5. KPI cards — 6 across, same label/value sizing.
6. Grid header row — white-on-gray, 600 weight.
7. Filter inputs — 24px height, `Ab` / `=` placeholders, same border.
8. Pagination text — `"Currently showing 1 to 20 of 11735"` format.
9. Download button at bottom — teal primary.

Fix any differences in `inventory.module.css` and re-screenshot.

- [ ] **Step 3: Final commit**

```bash
git commit -am "style: align inventory page with Mendix QA pixel parity"
```

---

## Self-Review Checklist

**Spec coverage:** Every element in the QA screenshot has a task:
- Sidebar → existing layout, not in this plan.
- Header (Inventory + week + buttons + last synced) → Task 2.2 / 4.2.
- KPI strip (6 cards) → Task 4.2 with data from Task 3.2.
- Grid with 10 columns + column selector → Task 4.2; column selector deferred (low-value customization — revisit in a follow-up).
- Row edit popup → Task 5.
- Excel download → Task 6.
- Pagination → Task 4.2.
- Create Auction button → Task 7 (stub).
- Refresh button → Task 4.2 (wired to `refresh()`).

**Placeholder scan:** No "TBD"s. Code blocks in every implementation step. Every referenced type/method is defined in an earlier task.

**Type consistency:**
- `AggregatedInventoryRow` — same 11 fields used in service, controller, exporter, frontend Zod schema.
- `InventoryPageResponse` — matches `AggregatedInventoryPageResponse` contract one-for-one.
- `updateInventoryRow` frontend body shape matches `AggregatedInventoryUpdateRequest` record.
- `WeekOption` fields match `Week` entity getters projected in the controller.

---

## Execution Handoff

**Plan complete and saved to `docs/tasks/aggregated-inventory-page-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** — Dispatch a fresh subagent per task, review between tasks, fast iteration.

**2. Inline Execution** — Execute tasks in this session using `superpowers:executing-plans`, batch execution with checkpoints.

**Which approach?**
