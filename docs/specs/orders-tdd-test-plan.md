# Orders Page — TDD Test Plan

> Companion document to `docs/specs/orders-page-migration.md`.
> Each section tells a developer exactly what tests to write BEFORE touching implementation code (RED phase), what minimal implementation makes them pass (GREEN phase), and which edge cases must not be missed.

---

## Conventions Established by This Project

Before reading the test plans, note the patterns already in use:

| Concern | Pattern |
|---------|---------|
| Unit tests | `@ExtendWith(MockitoExtension.class)`, `@InjectMocks`, `@Mock`, AssertJ |
| Controller tests | `@WebMvcTest`, `@MockBean`, `MockMvc`, JWT bearer token via `jwtService.generateToken()` |
| Integration tests | `@Testcontainers` + `PostgreSQLContainer`, Flyway disabled in `application-test.yml` |
| Test config | `src/test/resources/application-test.yml` (H2 in-memory, Flyway off) |
| Nesting | `@Nested` inner classes per method, `@DisplayName` on every test |
| Assertions | AssertJ `assertThat(...)` — never JUnit `assertEquals` |
| Exception assertions | `assertThatThrownBy(() -> ...).isInstanceOf(...).hasMessageContaining(...)` |

---

## Phase 1 — Backend Core (Database View + Service + Controller)

### 1.1 Unit Tests: `OrderHistoryServiceTest`

**File:** `backend/src/test/java/com/ecoatm/salesplatform/service/OrderHistoryServiceTest.java`

**Dependencies to mock:**
- `OrderHistoryViewRepository` (the JPA view repository)
- `BuyerCodeService` (provides buyer-code lists per user)
- `OfferItemRepository` (used in Phase 3 detail methods — stub here)
- `OrderRepository` (used in Phase 3 — stub here)

**Test class skeleton:**

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderHistoryServiceTest {

    @Mock private OrderHistoryViewRepository orderHistoryViewRepository;
    @Mock private BuyerCodeService buyerCodeService;
    @Mock private OfferItemRepository offerItemRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks private OrderHistoryService orderHistoryService;

    // All helper methods and @Nested classes below
}
```

---

#### Nested class: `ListOrders`

**Purpose:** Verify that `listOrders(tab, userId, pageable)` applies the correct Specification and returns mapped DTOs.

**Helper method to add to the test class:**

```java
private OrderHistoryView makeView(Long id, String orderNumber, String status,
                                   LocalDateTime lastUpdateDate, Long buyerCodeId) {
    OrderHistoryView v = new OrderHistoryView();
    // Use reflection or a package-private setter if the entity has no public setters.
    // Alternatively, expose a static factory method on the entity for test use.
    ReflectionTestUtils.setField(v, "id", id);
    ReflectionTestUtils.setField(v, "orderNumber", orderNumber);
    ReflectionTestUtils.setField(v, "orderStatus", status);
    ReflectionTestUtils.setField(v, "lastUpdateDate", lastUpdateDate);
    ReflectionTestUtils.setField(v, "buyerCodeId", buyerCodeId);
    ReflectionTestUtils.setField(v, "totalPrice", BigDecimal.ZERO);
    ReflectionTestUtils.setField(v, "skuCount", 1);
    ReflectionTestUtils.setField(v, "totalQuantity", 1);
    return v;
}

private BuyerCodeResponse makeCode(Long id) {
    return new BuyerCodeResponse(id, "BC-" + id, "Test Account", null, null);
}
```

**RED phase — write these tests FIRST:**

```java
@Nested
@DisplayName("listOrders")
class ListOrders {

    @Test
    @DisplayName("all tab: returns all orders scoped to user's buyer codes")
    void allTab_returnsScopedOrders() {
        // Arrange
        when(buyerCodeService.getBuyerCodesForUser(10L))
                .thenReturn(List.of(makeCode(1L), makeCode(2L)));
        OrderHistoryView row = makeView(100L, "ORD-001", "Ordered",
                LocalDateTime.now(), 1L);
        Page<OrderHistoryView> page = new PageImpl<>(List.of(row), PageRequest.of(0, 20), 1);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders("all", 10L, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).orderNumber()).isEqualTo("ORD-001");
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("recent tab: delegates to repository with lastUpdateDate filter")
    void recentTab_appliesDateFilter() {
        // Arrange
        when(buyerCodeService.getBuyerCodesForUser(10L))
                .thenReturn(List.of(makeCode(1L)));
        Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders("recent", 10L, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).isEmpty();
        // Verify the repository was called once — the Specification content is tested implicitly
        // via integration test (see section 1.3)
        verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("inProcess tab: delegates to repository with status IN filter")
    void inProcessTab_appliesStatusFilter() {
        when(buyerCodeService.getBuyerCodesForUser(10L))
                .thenReturn(List.of(makeCode(1L)));
        Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders("inProcess", 10L, PageRequest.of(0, 20));

        assertThat(result.getContent()).isEmpty();
        verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("complete tab: delegates to repository with status NOT IN filter")
    void completeTab_appliesStatusNotInFilter() {
        when(buyerCodeService.getBuyerCodesForUser(10L))
                .thenReturn(List.of(makeCode(1L)));
        Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders("complete", 10L, PageRequest.of(0, 20));

        assertThat(result.getContent()).isEmpty();
        verify(orderHistoryViewRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("returns empty page when user has no buyer codes")
    void noBuyerCodes_returnsEmptyPage() {
        // Arrange — user has zero buyer codes (common for new bidder accounts)
        when(buyerCodeService.getBuyerCodesForUser(99L)).thenReturn(List.of());
        // Repository should still be called — the Specification with empty IN list
        // will produce zero results without a SQL error
        Page<OrderHistoryView> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders("all", 99L, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("maps all DTO fields from view entity")
    void mapsDtoFieldsCorrectly() {
        // Arrange — populate every field on the view row
        when(buyerCodeService.getBuyerCodesForUser(10L))
                .thenReturn(List.of(makeCode(1L)));
        OrderHistoryView v = makeView(5L, "ORD-999", "Ordered",
                LocalDateTime.of(2025, 1, 15, 10, 0), 1L);
        ReflectionTestUtils.setField(v, "offerDate", LocalDateTime.of(2025, 1, 10, 8, 0));
        ReflectionTestUtils.setField(v, "orderDate", LocalDateTime.of(2025, 1, 12, 9, 0));
        ReflectionTestUtils.setField(v, "shipDate", LocalDateTime.of(2025, 1, 20, 12, 0));
        ReflectionTestUtils.setField(v, "shipMethod", "FedEx");
        ReflectionTestUtils.setField(v, "skuCount", 3);
        ReflectionTestUtils.setField(v, "totalQuantity", 10);
        ReflectionTestUtils.setField(v, "totalPrice", new BigDecimal("450.00"));
        ReflectionTestUtils.setField(v, "buyer", "Jane Doe");
        ReflectionTestUtils.setField(v, "company", "TechCo LLC");
        ReflectionTestUtils.setField(v, "offerOrderType", "Order");
        ReflectionTestUtils.setField(v, "offerId", 42L);

        Page<OrderHistoryView> page = new PageImpl<>(List.of(v), PageRequest.of(0, 20), 1);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<OrderHistoryResponse> result =
                orderHistoryService.listOrders("all", 10L, PageRequest.of(0, 20));

        // Assert — every mapped field
        OrderHistoryResponse dto = result.getContent().get(0);
        assertThat(dto.id()).isEqualTo(5L);
        assertThat(dto.orderNumber()).isEqualTo("ORD-999");
        assertThat(dto.orderStatus()).isEqualTo("Ordered");
        assertThat(dto.shipMethod()).isEqualTo("FedEx");
        assertThat(dto.skuCount()).isEqualTo(3);
        assertThat(dto.totalQuantity()).isEqualTo(10);
        assertThat(dto.totalPrice()).isEqualByComparingTo("450.00");
        assertThat(dto.buyer()).isEqualTo("Jane Doe");
        assertThat(dto.company()).isEqualTo("TechCo LLC");
        assertThat(dto.offerOrderType()).isEqualTo("Order");
        assertThat(dto.offerId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("throws IllegalArgumentException for unrecognized tab name")
    void unknownTab_throwsIllegalArgumentException() {
        when(buyerCodeService.getBuyerCodesForUser(10L))
                .thenReturn(List.of(makeCode(1L)));

        assertThatThrownBy(() ->
                orderHistoryService.listOrders("bogusTab", 10L, PageRequest.of(0, 20)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bogusTab");
    }

    @Test
    @DisplayName("null userId: throws NullPointerException or IllegalArgumentException")
    void nullUserId_throws() {
        assertThatThrownBy(() ->
                orderHistoryService.listOrders("all", null, PageRequest.of(0, 20)))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
    }
}
```

**GREEN phase:** Implement `listOrders` with:
1. Guard clause for `userId == null`.
2. Guard clause for unrecognized tab — throw `IllegalArgumentException("Unknown tab: " + tab)`.
3. Call `buyerCodeService.getBuyerCodesForUser(userId)` and extract IDs.
4. Build `Specification` as `scopeByUser(userId).and(tabFilter(tab))`.
5. Return `repository.findAll(spec, pageable).map(OrderHistoryResponse::from)`.

---

#### Nested class: `GetTabCounts`

```java
@Nested
@DisplayName("getTabCounts")
class GetTabCounts {

    @Test
    @DisplayName("returns correct counts for each tab")
    void returnsCountsForAllTabs() {
        // Arrange
        when(buyerCodeService.getBuyerCodesForUser(10L))
                .thenReturn(List.of(makeCode(1L), makeCode(2L)));
        // Stub the four count queries
        when(orderHistoryViewRepository.count(argThat(spec -> spec != null))) // recent
                .thenReturn(5L)
                .thenReturn(3L)   // inProcess
                .thenReturn(12L)  // complete
                .thenReturn(20L); // all

        // Act
        OrderHistoryTabCounts counts = orderHistoryService.getTabCounts(10L);

        // Assert
        assertThat(counts.recent()).isEqualTo(5L);
        assertThat(counts.inProcess()).isEqualTo(3L);
        assertThat(counts.complete()).isEqualTo(12L);
        assertThat(counts.all()).isEqualTo(20L);
    }

    @Test
    @DisplayName("returns all-zero counts when user has no buyer codes")
    void noBuyerCodes_returnsZeroCounts() {
        when(buyerCodeService.getBuyerCodesForUser(99L)).thenReturn(List.of());
        // Even with empty buyer codes, count queries still execute; repo returns 0
        when(orderHistoryViewRepository.count(any(Specification.class))).thenReturn(0L);

        OrderHistoryTabCounts counts = orderHistoryService.getTabCounts(99L);

        assertThat(counts.recent()).isZero();
        assertThat(counts.inProcess()).isZero();
        assertThat(counts.complete()).isZero();
        assertThat(counts.all()).isZero();
    }

    @Test
    @DisplayName("null userId: throws")
    void nullUserId_throws() {
        assertThatThrownBy(() -> orderHistoryService.getTabCounts(null))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
    }
}
```

**GREEN phase:** Issue four `repository.count(spec)` calls using the same scoping + tab-filter Specification building pattern. Return `new OrderHistoryTabCounts(recent, inProcess, complete, all)`.

> Implementation note on mocking the four counts: because all four calls receive a `Specification` argument, `when(...).thenReturn(...)` chaining with consecutive `.thenReturn` works because Mockito returns each value in sequence on successive invocations of the same matcher. If the implementation calls a named method per tab (e.g., `countRecent(spec)`) the stubs must be adjusted to match distinct method signatures.

---

#### Nested class: `BuyerCodeScoping`

These tests verify the authorization boundary — the single most security-critical path in this feature.

```java
@Nested
@DisplayName("buyer-code scoping authorization")
class BuyerCodeScoping {

    @Test
    @DisplayName("admin user: receives all buyer codes and sees all orders")
    void adminUser_seesAllBuyerCodes() {
        // Arrange — admin gets a large list back from BuyerCodeService
        List<BuyerCodeResponse> allCodes = List.of(
                makeCode(1L), makeCode(2L), makeCode(3L));
        when(buyerCodeService.getBuyerCodesForUser(1L)).thenReturn(allCodes);
        Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // Act
        orderHistoryService.listOrders("all", 1L, PageRequest.of(0, 20));

        // Assert — BuyerCodeService was called with the admin's userId
        verify(buyerCodeService).getBuyerCodesForUser(1L);
    }

    @Test
    @DisplayName("bidder user: BuyerCodeService called with bidder's userId")
    void bidderUser_buyerCodeServiceCalledWithBidderId() {
        // Arrange — bidder gets only their linked codes
        when(buyerCodeService.getBuyerCodesForUser(50L))
                .thenReturn(List.of(makeCode(7L)));
        Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        // Act
        orderHistoryService.listOrders("all", 50L, PageRequest.of(0, 20));

        // Assert — scoping delegate was called with bidder's userId, not a hardcoded admin ID
        verify(buyerCodeService).getBuyerCodesForUser(50L);
        // The repository Specification will contain only buyerCodeId=7 — verified via integration test
    }

    @Test
    @DisplayName("BuyerCodeService is always called (never bypassed for any role)")
    void buyerCodeServiceAlwaysCalled_forAllTab() {
        when(buyerCodeService.getBuyerCodesForUser(any())).thenReturn(List.of(makeCode(1L)));
        Page<OrderHistoryView> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryViewRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        orderHistoryService.listOrders("all", 1L, PageRequest.of(0, 20));

        // This verify catches accidental bypass paths where the service skips scoping
        verify(buyerCodeService, atLeastOnce()).getBuyerCodesForUser(1L);
    }
}
```

**GREEN phase:** The `scopeByUser(userId)` private method must always delegate to `BuyerCodeService` — never bypass it based on a hardcoded role check inside `OrderHistoryService`. Role-based access control lives entirely in `BuyerCodeService`.

---

### 1.2 Controller Tests: `OrderHistoryControllerTest`

**File:** `backend/src/test/java/com/ecoatm/salesplatform/controller/OrderHistoryControllerTest.java`

**Test class setup (mirrors `PricingControllerTest`):**

```java
@WebMvcTest(OrderHistoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class OrderHistoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private OrderHistoryService orderHistoryService;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private OrderHistoryResponse makeOrderResponse(Long id, String orderNumber) {
        return new OrderHistoryResponse(
                id, orderNumber,
                LocalDateTime.of(2025, 1, 10, 8, 0),
                LocalDateTime.of(2025, 1, 12, 9, 0),
                "Ordered", null, null, 2, 5,
                new BigDecimal("200.00"), "Jane Doe", "TechCo",
                LocalDateTime.of(2025, 1, 12, 9, 0), "Order", id
        );
    }
}
```

**RED phase tests:**

```java
@Nested
@DisplayName("GET /api/v1/pws/orders")
class ListOrders {

    @Test
    @DisplayName("returns 401 without auth token")
    void requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/pws/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("returns paginated orders for authenticated user")
    void returnsPaginatedOrders() throws Exception {
        // Arrange
        OrderHistoryResponse dto = makeOrderResponse(1L, "ORD-001");
        Page<OrderHistoryResponse> page = new PageImpl<>(
                List.of(dto), PageRequest.of(0, 20), 1);
        when(orderHistoryService.listOrders(eq("all"), eq(1L), any(Pageable.class)))
                .thenReturn(page);

        // Act + Assert
        mockMvc.perform(get("/api/v1/pws/orders")
                        .param("tab", "all")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.content[0].totalPrice").value(200.00))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @DisplayName("defaults tab to 'all' when not provided")
    void defaultsTabToAll() throws Exception {
        Page<OrderHistoryResponse> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryService.listOrders(eq("all"), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/pws/orders")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(orderHistoryService).listOrders(eq("all"), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("passes tab and pagination params to service")
    void passesTabAndPagination() throws Exception {
        Page<OrderHistoryResponse> page = new PageImpl<>(List.of(), PageRequest.of(1, 50), 0);
        when(orderHistoryService.listOrders(eq("inProcess"), eq(2L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/pws/orders")
                        .param("tab", "inProcess")
                        .param("userId", "2")
                        .param("page", "1")
                        .param("size", "50")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk());

        verify(orderHistoryService).listOrders(eq("inProcess"), eq(2L), any(Pageable.class));
    }

    @Test
    @DisplayName("returns 400 when service throws IllegalArgumentException for bad tab")
    void returns400ForUnknownTab() throws Exception {
        when(orderHistoryService.listOrders(eq("badTab"), any(), any(Pageable.class)))
                .thenThrow(new IllegalArgumentException("Unknown tab: badTab"));

        mockMvc.perform(get("/api/v1/pws/orders")
                        .param("tab", "badTab")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("returns 200 with empty content array when no results")
    void returnsEmptyContentArray() throws Exception {
        Page<OrderHistoryResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(orderHistoryService.listOrders(any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/pws/orders")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}

@Nested
@DisplayName("GET /api/v1/pws/orders/counts")
class GetTabCounts {

    @Test
    @DisplayName("returns 401 without auth token")
    void requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/pws/orders/counts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("returns tab counts for authenticated user")
    void returnsTabCounts() throws Exception {
        when(orderHistoryService.getTabCounts(1L))
                .thenReturn(new OrderHistoryTabCounts(5L, 3L, 12L, 20L));

        mockMvc.perform(get("/api/v1/pws/orders/counts")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recent").value(5))
                .andExpect(jsonPath("$.inProcess").value(3))
                .andExpect(jsonPath("$.complete").value(12))
                .andExpect(jsonPath("$.all").value(20));
    }

    @Test
    @DisplayName("returns all-zero counts when service returns zeros")
    void returnsZeroCounts() throws Exception {
        when(orderHistoryService.getTabCounts(99L))
                .thenReturn(new OrderHistoryTabCounts(0L, 0L, 0L, 0L));

        mockMvc.perform(get("/api/v1/pws/orders/counts")
                        .param("userId", "99")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(0));
    }
}
```

**GREEN phase:** Implement `OrderHistoryController` with:
- `@GetMapping` for `/orders` delegating to `orderHistoryService.listOrders(tab, userId, pageable)` with `tab` defaulting to `"all"`.
- `@GetMapping` for `/orders/counts` delegating to `orderHistoryService.getTabCounts(userId)`.
- `@ExceptionHandler(IllegalArgumentException.class)` returning `400 Bad Request` (reuse the existing global handler if one exists in the project, otherwise add to this controller).

---

### 1.3 Repository Integration Test: `OrderHistoryViewRepositoryIT`

**File:** `backend/src/test/java/com/ecoatm/salesplatform/repository/OrderHistoryViewRepositoryIT.java`

**Purpose:** Verify the `offer_and_orders_view` VIEW produces correct rows and the Specification predicates filter correctly. Uses Testcontainers + a dedicated Flyway migration so the real SQL is exercised.

**Test class skeleton:**

```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class OrderHistoryViewRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("salesplatform_test")
                    .withUsername("salesplatform")
                    .withPassword("salesplatform");

    @DynamicPropertySource
    static void overrideDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired private OrderHistoryViewRepository repository;
    @Autowired private JdbcTemplate jdbcTemplate;
    // or use a TestDataHelper that inserts seed rows directly

    // Seed method — inserts minimal rows into pws.offer, pws.order,
    // buyer_mgmt.buyer_code, buyer_mgmt.buyer_code_buyers, buyer_mgmt.buyers
    @BeforeEach
    void seedDatabase() {
        // Insert into each required table in FK order.
        // See Section 1.3 seed SQL below.
    }
}
```

**Seed SQL to insert inside `@BeforeEach` using `JdbcTemplate`:**

```sql
-- buyer_mgmt schema
INSERT INTO buyer_mgmt.buyer_code (id, code) VALUES (10, 'BC-TEST-1');
INSERT INTO buyer_mgmt.buyers (id, first_name, last_name, company_name) VALUES (20, 'Jane', 'Doe', 'TechCo LLC');
INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id) VALUES (10, 20);

-- pws.offer (status must be in VIEW WHERE clause)
INSERT INTO pws.offer (id, offer_number, status, buyer_code_id, submission_date,
                       total_qty, total_price, updated_date)
VALUES (100, 'OFF-100', 'Ordered', 10,
        NOW() - INTERVAL '1 day', 5, 250.00, NOW() - INTERVAL '1 hour');

-- pws.order linked to offer
INSERT INTO pws."order" (id, offer_id, order_number, order_status, order_date,
                         ship_date, ship_method, updated_date)
VALUES (200, 100, 'ORD-100', 'Ordered', NOW() - INTERVAL '12 hours',
        NOW() + INTERVAL '3 days', 'FedEx', NOW());

-- A second offer with Pending_Order status (no linked order)
INSERT INTO pws.offer (id, offer_number, status, buyer_code_id, submission_date,
                       total_qty, total_price, updated_date)
VALUES (101, 'OFF-101', 'Pending_Order', 10,
        NOW() - INTERVAL '10 days', 2, 80.00, NOW() - INTERVAL '10 days');

-- An offer with status outside the VIEW filter (should NOT appear)
INSERT INTO pws.offer (id, offer_number, status, buyer_code_id, submission_date,
                       total_qty, total_price, updated_date)
VALUES (102, 'OFF-102', 'Draft', 10,
        NOW() - INTERVAL '5 days', 1, 50.00, NOW() - INTERVAL '5 days');
```

**RED phase tests:**

```java
@Nested
@DisplayName("VIEW query correctness")
class ViewQueryCorrectness {

    @Test
    @DisplayName("includes offers with linked orders")
    void includesOrderedOffers() {
        List<OrderHistoryView> all = repository.findAll();
        assertThat(all).extracting(OrderHistoryView::getOrderNumber)
                .contains("ORD-100");
    }

    @Test
    @DisplayName("includes offers without linked orders (Pending_Order status)")
    void includesOffersWithoutOrders() {
        List<OrderHistoryView> all = repository.findAll();
        // offerId=101, no order row — orderNumber falls back to offer_number
        assertThat(all).extracting(OrderHistoryView::getOrderNumber)
                .contains("OFF-101");
    }

    @Test
    @DisplayName("excludes offers with status outside VIEW filter (Draft)")
    void excludesDraftOffers() {
        List<OrderHistoryView> all = repository.findAll();
        assertThat(all).extracting(OrderHistoryView::getOrderNumber)
                .doesNotContain("OFF-102");
    }

    @Test
    @DisplayName("view row has correct buyer and company from correlated subquery")
    void buyerAndCompanyFromSubquery() {
        List<OrderHistoryView> all = repository.findAll();
        Optional<OrderHistoryView> row = all.stream()
                .filter(v -> "ORD-100".equals(v.getOrderNumber()))
                .findFirst();
        assertThat(row).isPresent();
        assertThat(row.get().getBuyer()).isEqualTo("Jane Doe");
        assertThat(row.get().getCompany()).isEqualTo("TechCo LLC");
    }

    @Test
    @DisplayName("view row id is positive for rows with linked order")
    void rowIdPositiveForLinkedOrder() {
        List<OrderHistoryView> all = repository.findAll();
        Optional<OrderHistoryView> row = all.stream()
                .filter(v -> "ORD-100".equals(v.getOrderNumber()))
                .findFirst();
        assertThat(row).isPresent();
        assertThat(row.get().getId()).isPositive();
    }

    @Test
    @DisplayName("view row id is negative for offer-only rows (no linked order)")
    void rowIdNegativeForOfferOnly() {
        List<OrderHistoryView> all = repository.findAll();
        Optional<OrderHistoryView> row = all.stream()
                .filter(v -> "OFF-101".equals(v.getOrderNumber()))
                .findFirst();
        assertThat(row).isPresent();
        assertThat(row.get().getId()).isNegative();
    }
}

@Nested
@DisplayName("Specification predicates")
class SpecificationPredicates {

    private static final List<String> IN_PROCESS_STATUSES =
            List.of("In Process", "Offer Pending", "Awaiting Carrier Pickup");

    @Test
    @DisplayName("buyerCodeId IN predicate filters to specific buyer code")
    void buyerCodeInPredicateFilters() {
        // Only buyer code 10 is seeded — use a different code to get zero results
        Specification<OrderHistoryView> spec =
                (root, q, cb) -> root.get("buyerCodeId").in(List.of(999L));
        long count = repository.count(spec);
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("buyerCodeId IN predicate returns rows for known code")
    void buyerCodeInPredicateReturnsRows() {
        Specification<OrderHistoryView> spec =
                (root, q, cb) -> root.get("buyerCodeId").in(List.of(10L));
        long count = repository.count(spec);
        assertThat(count).isGreaterThanOrEqualTo(2); // ORD-100 and OFF-101
    }

    @Test
    @DisplayName("lastUpdateDate >= cutoff filters to recent rows")
    void recentDatePredicateFilters() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        Specification<OrderHistoryView> spec =
                (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdateDate"), cutoff);
        // ORD-100 updated ~1 hour ago — should be included
        // OFF-101 updated 10 days ago — should be excluded
        List<OrderHistoryView> results = repository.findAll(spec);
        assertThat(results).extracting(OrderHistoryView::getOrderNumber)
                .contains("ORD-100")
                .doesNotContain("OFF-101");
    }

    @Test
    @DisplayName("orderStatus IN filter returns only matching rows")
    void statusInPredicateFilters() {
        // Seed an in-process offer for this test
        jdbcTemplate.update("""
            INSERT INTO pws.offer (id, offer_number, status, buyer_code_id,
                submission_date, total_qty, total_price, updated_date)
            VALUES (103, 'OFF-103', 'Offer Pending', 10, NOW(), 1, 10.00, NOW())
        """);
        Specification<OrderHistoryView> spec =
                (root, q, cb) -> root.get("orderStatus").in(IN_PROCESS_STATUSES);
        List<OrderHistoryView> results = repository.findAll(spec);
        assertThat(results).extracting(OrderHistoryView::getOrderNumber)
                .contains("OFF-103")
                .doesNotContain("ORD-100");
    }
}
```

**GREEN phase:** The VIEW SQL in `V37__order_history_view.sql` plus the `@Entity` + `@Immutable` entity mapping is the complete implementation. No Java code is required to make repository integration tests pass beyond the entity and repository interface.

---

## Phase 3 — Order Details

### 3.1 Unit Tests: additions to `OrderHistoryServiceTest`

Add these `@Nested` classes to the same `OrderHistoryServiceTest` created in Phase 1.

**Additional mock needed:**

```java
@Mock private OfferItemRepository offerItemRepository;
@Mock private OrderRepository orderRepository;
```

**Helper for `OfferItem`:**

```java
private OfferItem makeOfferItem(Long id, Long offerId, String sku,
                                 Integer orderedQty, Integer shippedQty,
                                 BigDecimal unitPrice) {
    OfferItem item = new OfferItem();
    item.setId(id);
    item.setOfferId(offerId);
    item.setSku(sku);
    item.setOrderedQty(orderedQty);
    item.setShippedQty(shippedQty);
    item.setUnitPrice(unitPrice);
    // description can be derived from SKU or set separately
    return item;
}
```

**RED phase tests:**

```java
@Nested
@DisplayName("getDetailsBySku")
class GetDetailsBySku {

    @Test
    @DisplayName("returns SKU-level detail rows for a valid offerId")
    void returnsSkuDetailRows() {
        // Arrange
        OfferItem item1 = makeOfferItem(1L, 42L, "PWS-001", 5, 5,
                new BigDecimal("50.00"));
        OfferItem item2 = makeOfferItem(2L, 42L, "PWS-002", 3, 2,
                new BigDecimal("30.00"));
        when(offerItemRepository.findByOfferId(42L))
                .thenReturn(List.of(item1, item2));

        // Act
        List<OrderDetailBySkuResponse> result =
                orderHistoryService.getDetailsBySku(42L);

        // Assert
        assertThat(result).hasSize(2);
        OrderDetailBySkuResponse first = result.get(0);
        assertThat(first.sku()).isEqualTo("PWS-001");
        assertThat(first.orderedQty()).isEqualTo(5);
        assertThat(first.shippedQty()).isEqualTo(5);
        assertThat(first.unitPrice()).isEqualByComparingTo("50.00");
        assertThat(first.totalPrice()).isEqualByComparingTo("250.00"); // 5 * 50.00
    }

    @Test
    @DisplayName("returns empty list when offer has no items")
    void emptyOffer_returnsEmptyList() {
        when(offerItemRepository.findByOfferId(999L)).thenReturn(List.of());

        List<OrderDetailBySkuResponse> result =
                orderHistoryService.getDetailsBySku(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("handles null shippedQty gracefully (maps to zero)")
    void nullShippedQty_mapsToZero() {
        OfferItem item = makeOfferItem(1L, 42L, "PWS-001", 5, null,
                new BigDecimal("50.00"));
        when(offerItemRepository.findByOfferId(42L)).thenReturn(List.of(item));

        List<OrderDetailBySkuResponse> result =
                orderHistoryService.getDetailsBySku(42L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).shippedQty()).isZero();
    }

    @Test
    @DisplayName("totalPrice is orderedQty * unitPrice")
    void totalPriceCalculation() {
        OfferItem item = makeOfferItem(1L, 42L, "PWS-003", 4, 4,
                new BigDecimal("25.00"));
        when(offerItemRepository.findByOfferId(42L)).thenReturn(List.of(item));

        List<OrderDetailBySkuResponse> result =
                orderHistoryService.getDetailsBySku(42L);

        assertThat(result.get(0).totalPrice()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("null offerId: throws")
    void nullOfferId_throws() {
        assertThatThrownBy(() -> orderHistoryService.getDetailsBySku(null))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
    }
}

@Nested
@DisplayName("getDetailsByDevice")
class GetDetailsByDevice {

    @Test
    @DisplayName("returns device-level rows from order with JSON content")
    void returnsDeviceRows() {
        // Arrange — the Order entity has a jsonContent field with device details
        Order order = new Order();
        order.setId(200L);
        // Assuming parseDeviceDetails() extracts IMEI, serial, box, tracking from JSON
        // The JSON structure must match what Deposco sync populates (RISK 5 in spec)
        order.setJsonContent("""
            [
              {"imei":"123456789","sku":"PWS-001","description":"iPhone 15",
               "unitPrice":250.00,"serialNumber":"SN-001","boxNumber":"BX-01",
               "trackingNumber":"1Z999AA1","trackingUrl":"https://track.example.com/1Z999AA1"},
              {"imei":"987654321","sku":"PWS-001","description":"iPhone 15",
               "unitPrice":250.00,"serialNumber":"SN-002","boxNumber":"BX-02",
               "trackingNumber":"1Z999AA1","trackingUrl":"https://track.example.com/1Z999AA1"}
            ]
        """);
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order));

        // Act
        List<OrderDetailByDeviceResponse> result =
                orderHistoryService.getDetailsByDevice(200L);

        // Assert
        assertThat(result).hasSize(2);
        OrderDetailByDeviceResponse row = result.get(0);
        assertThat(row.imei()).isEqualTo("123456789");
        assertThat(row.sku()).isEqualTo("PWS-001");
        assertThat(row.trackingNumber()).isEqualTo("1Z999AA1");
        assertThat(row.trackingUrl()).contains("1Z999AA1");
    }

    @Test
    @DisplayName("returns empty list when order not found")
    void orderNotFound_returnsEmptyList() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        List<OrderDetailByDeviceResponse> result =
                orderHistoryService.getDetailsByDevice(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("returns empty list when order JSON content is null")
    void nullJsonContent_returnsEmptyList() {
        Order order = new Order();
        order.setId(200L);
        order.setJsonContent(null);
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order));

        List<OrderDetailByDeviceResponse> result =
                orderHistoryService.getDetailsByDevice(200L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("returns empty list when order JSON content is empty array")
    void emptyJsonArray_returnsEmptyList() {
        Order order = new Order();
        order.setId(200L);
        order.setJsonContent("[]");
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order));

        List<OrderDetailByDeviceResponse> result =
                orderHistoryService.getDetailsByDevice(200L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null orderId: throws")
    void nullOrderId_throws() {
        assertThatThrownBy(() -> orderHistoryService.getDetailsByDevice(null))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
    }
}
```

**GREEN phase:** Implement `getDetailsBySku` as a `findByOfferId` call then `map` to `OrderDetailBySkuResponse`. Implement `getDetailsByDevice` with `findById` then JSON parse of `jsonContent` into `List<OrderDetailByDeviceResponse>` (use Jackson `ObjectMapper`). Handle null/empty JSON gracefully.

---

### 3.2 Strikethrough Logic Tests

The spec calls for conditional strikethrough on rows where status is `"Declined"` or `"Canceled"`. This is frontend CSS logic but the service can expose a helper or the response DTO can include a flag.

Add to `OrderHistoryServiceTest` or create `OrderHistoryResponseTest.java`:

**File:** `backend/src/test/java/com/ecoatm/salesplatform/dto/OrderHistoryResponseTest.java`

```java
class OrderHistoryResponseTest {

    @Test
    @DisplayName("isStrikethrough returns true for Declined status")
    void strikethrough_declined() {
        OrderHistoryResponse dto = makeResponse("Declined");
        assertThat(dto.isStrikethrough()).isTrue();
    }

    @Test
    @DisplayName("isStrikethrough returns true for Canceled status")
    void strikethrough_canceled() {
        OrderHistoryResponse dto = makeResponse("Canceled");
        assertThat(dto.isStrikethrough()).isTrue();
    }

    @Test
    @DisplayName("isStrikethrough returns false for Ordered status")
    void noStrikethrough_ordered() {
        OrderHistoryResponse dto = makeResponse("Ordered");
        assertThat(dto.isStrikethrough()).isFalse();
    }

    @Test
    @DisplayName("isStrikethrough returns false for null status")
    void noStrikethrough_nullStatus() {
        OrderHistoryResponse dto = makeResponse(null);
        assertThat(dto.isStrikethrough()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"In Process", "Offer Pending", "Awaiting Carrier Pickup", "Ordered", "Pending_Order"})
    @DisplayName("isStrikethrough returns false for active statuses")
    void noStrikethrough_activeStatuses(String status) {
        OrderHistoryResponse dto = makeResponse(status);
        assertThat(dto.isStrikethrough()).isFalse();
    }

    private OrderHistoryResponse makeResponse(String status) {
        return new OrderHistoryResponse(
                1L, "ORD-001",
                LocalDateTime.now(), LocalDateTime.now(),
                status, null, null, 1, 1,
                BigDecimal.TEN, "Buyer", "Company",
                LocalDateTime.now(), "Order", 1L
        );
    }
}
```

**GREEN phase:** Add `default boolean isStrikethrough()` to the `OrderHistoryResponse` record:

```java
default boolean isStrikethrough() {
    return "Declined".equals(orderStatus()) || "Canceled".equals(orderStatus());
}
```

Or, if `OrderHistoryResponse` is a plain record without default methods, add `isStrikethrough` as a derived field in the `from(OrderHistoryView)` factory method.

---

### 3.3 Controller Tests for Detail Endpoints

Add to `OrderHistoryControllerTest.java`:

```java
@Nested
@DisplayName("GET /api/v1/pws/orders/{offerId}/details/by-sku")
class GetDetailsBySku {

    @Test
    @DisplayName("returns 401 without auth")
    void requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/pws/orders/42/details/by-sku"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("returns SKU detail rows for valid offerId")
    void returnsSkuRows() throws Exception {
        OrderDetailBySkuResponse row = new OrderDetailBySkuResponse(
                "PWS-001", "iPhone 15", 5, 5,
                new BigDecimal("50.00"), new BigDecimal("250.00"));
        when(orderHistoryService.getDetailsBySku(42L)).thenReturn(List.of(row));

        mockMvc.perform(get("/api/v1/pws/orders/42/details/by-sku")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("PWS-001"))
                .andExpect(jsonPath("$[0].orderedQty").value(5))
                .andExpect(jsonPath("$[0].totalPrice").value(250.00));
    }

    @Test
    @DisplayName("returns empty array when no items for offerId")
    void returnsEmptyArray() throws Exception {
        when(orderHistoryService.getDetailsBySku(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pws/orders/999/details/by-sku")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}

@Nested
@DisplayName("GET /api/v1/pws/orders/{orderId}/details/by-device")
class GetDetailsByDevice {

    @Test
    @DisplayName("returns 401 without auth")
    void requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/pws/orders/200/details/by-device"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("returns device rows with tracking URL")
    void returnsDeviceRowsWithTracking() throws Exception {
        OrderDetailByDeviceResponse row = new OrderDetailByDeviceResponse(
                "123456789", "PWS-001", "iPhone 15",
                new BigDecimal("250.00"), "SN-001", "BX-01",
                "1Z999AA1", "https://track.example.com/1Z999AA1");
        when(orderHistoryService.getDetailsByDevice(200L)).thenReturn(List.of(row));

        mockMvc.perform(get("/api/v1/pws/orders/200/details/by-device")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imei").value("123456789"))
                .andExpect(jsonPath("$[0].trackingUrl").value("https://track.example.com/1Z999AA1"));
    }

    @Test
    @DisplayName("returns empty array when order has no device detail")
    void returnsEmptyArray() throws Exception {
        when(orderHistoryService.getDetailsByDevice(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pws/orders/999/details/by-device")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
```

---

## Phase 4 — Excel Export

### 4.1 Prerequisite: `pom.xml` dependency

Add to `backend/pom.xml` before writing any export tests. The test for the binary output requires POI to be on the classpath.

```xml
<!-- Apache POI — XLSX generation -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.3.0</version>
</dependency>
```

This is a Phase 4 prerequisite — without it the import statements in the test will not compile, making the RED phase genuinely red for the right reason.

---

### 4.2 Unit Tests for Export: additions to `OrderHistoryServiceTest`

```java
@Nested
@DisplayName("exportExcel")
class ExportExcel {

    // Helper: build a list of N order responses for export testing
    private List<OrderHistoryView> makeViewList(int count) {
        List<OrderHistoryView> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(makeView((long) i, "ORD-" + i, "Ordered",
                    LocalDateTime.now(), 1L));
        }
        return list;
    }

    @Test
    @DisplayName("returns non-empty byte array")
    void returnsNonEmptyByteArray() {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));
        when(orderHistoryViewRepository.findAll(any(Specification.class)))
                .thenReturn(makeViewList(3));

        byte[] result = orderHistoryService.exportExcel("all", 1L);

        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("output is a valid XLSX workbook")
    void outputIsValidXlsx() throws Exception {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));
        when(orderHistoryViewRepository.findAll(any(Specification.class)))
                .thenReturn(makeViewList(2));

        byte[] result = orderHistoryService.exportExcel("all", 1L);

        // XSSFWorkbook will throw if bytes are not valid OOXML
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            assertThat(wb.getNumberOfSheets()).isGreaterThanOrEqualTo(1);
        }
    }

    @Test
    @DisplayName("first row contains correct column headers")
    void firstRowIsHeaderRow() throws Exception {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));
        when(orderHistoryViewRepository.findAll(any(Specification.class)))
                .thenReturn(makeViewList(1));

        byte[] result = orderHistoryService.exportExcel("all", 1L);

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            Row header = wb.getSheetAt(0).getRow(0);
            // Exact column order from spec:
            // OrderNumber, OfferDate, OrderDate, OrderStatus, SKUcount,
            // TotalQuantity, TotalPrice, Buyer, Company, LastUpdateDate,
            // OfferOrderType, ShipDate, ShipMethod
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("OrderNumber");
            assertThat(header.getCell(1).getStringCellValue()).isEqualTo("OfferDate");
            assertThat(header.getCell(2).getStringCellValue()).isEqualTo("OrderDate");
            assertThat(header.getCell(3).getStringCellValue()).isEqualTo("OrderStatus");
            assertThat(header.getCell(4).getStringCellValue()).isEqualTo("SKUcount");
            assertThat(header.getCell(5).getStringCellValue()).isEqualTo("TotalQuantity");
            assertThat(header.getCell(6).getStringCellValue()).isEqualTo("TotalPrice");
            assertThat(header.getCell(7).getStringCellValue()).isEqualTo("Buyer");
            assertThat(header.getCell(8).getStringCellValue()).isEqualTo("Company");
            assertThat(header.getCell(9).getStringCellValue()).isEqualTo("LastUpdateDate");
            assertThat(header.getCell(10).getStringCellValue()).isEqualTo("OfferOrderType");
            assertThat(header.getCell(11).getStringCellValue()).isEqualTo("ShipDate");
            assertThat(header.getCell(12).getStringCellValue()).isEqualTo("ShipMethod");
        }
    }

    @Test
    @DisplayName("data rows contain correct values")
    void dataRowsHaveCorrectValues() throws Exception {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));

        OrderHistoryView v = makeView(1L, "ORD-XYZ", "Ordered",
                LocalDateTime.of(2025, 3, 1, 0, 0), 1L);
        ReflectionTestUtils.setField(v, "buyer", "Jane Doe");
        ReflectionTestUtils.setField(v, "company", "TechCo LLC");
        ReflectionTestUtils.setField(v, "totalPrice", new BigDecimal("500.00"));
        ReflectionTestUtils.setField(v, "skuCount", 3);
        ReflectionTestUtils.setField(v, "totalQuantity", 7);

        when(orderHistoryViewRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(v));

        byte[] result = orderHistoryService.exportExcel("all", 1L);

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            Row dataRow = wb.getSheetAt(0).getRow(1); // row 0 is header
            assertThat(dataRow.getCell(0).getStringCellValue()).isEqualTo("ORD-XYZ");
            assertThat(dataRow.getCell(3).getStringCellValue()).isEqualTo("Ordered");
            assertThat(dataRow.getCell(7).getStringCellValue()).isEqualTo("Jane Doe");
            assertThat(dataRow.getCell(8).getStringCellValue()).isEqualTo("TechCo LLC");
        }
    }

    @Test
    @DisplayName("empty result set produces valid file with header row only")
    void emptyData_producesHeaderOnlyFile() throws Exception {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));
        when(orderHistoryViewRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        byte[] result = orderHistoryService.exportExcel("all", 1L);

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            Sheet sheet = wb.getSheetAt(0);
            assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(1); // header only
            // Header row must still be present
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("OrderNumber");
        }
    }

    @Test
    @DisplayName("inProcess tab applies status filter before export")
    void inProcessTab_appliesFilterBeforeExport() {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));
        when(orderHistoryViewRepository.findAll(any(Specification.class)))
                .thenReturn(makeViewList(5));

        byte[] result = orderHistoryService.exportExcel("inProcess", 1L);

        // Verify the repository was called — Specification content tested in integration test
        assertThat(result).isNotEmpty();
        verify(orderHistoryViewRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("unknown tab throws IllegalArgumentException")
    void unknownTab_throwsIllegalArgumentException() {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));

        assertThatThrownBy(() -> orderHistoryService.exportExcel("badTab", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("badTab");
    }

    @Test
    @DisplayName("large dataset (1000+ rows) produces valid file within timeout")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void largeDataset_producesFileWithinTimeout() throws Exception {
        when(buyerCodeService.getBuyerCodesForUser(1L))
                .thenReturn(List.of(makeCode(1L)));
        when(orderHistoryViewRepository.findAll(any(Specification.class)))
                .thenReturn(makeViewList(1500));

        byte[] result = orderHistoryService.exportExcel("all", 1L);

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            // 1500 data rows + 1 header row
            assertThat(wb.getSheetAt(0).getPhysicalNumberOfRows()).isEqualTo(1501);
        }
    }
}
```

**GREEN phase:**
1. Add `poi-ooxml` dependency to `pom.xml`.
2. Add `exportExcel(String tab, Long userId)` to `OrderHistoryService`:
   - Get buyer codes via `buyerCodeService.getBuyerCodesForUser(userId)`.
   - Build `Specification` the same way as `listOrders` (reuse the same private `tabFilter` + `scopeByUser` methods).
   - Call `repository.findAll(spec)` (no pagination — full result set).
   - Create `XSSFWorkbook`, write header row with the 13 column names from the spec, then one row per result.
   - Return `workbook.write(baos); return baos.toByteArray()`.
3. Add `@GetMapping("/export")` in controller that calls `exportExcel`, sets `Content-Disposition: attachment; filename=...` header and returns `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`.

---

### 4.3 Controller Tests for Export Endpoint

Add to `OrderHistoryControllerTest.java`:

```java
@Nested
@DisplayName("GET /api/v1/pws/orders/export")
class ExportExcel {

    @Test
    @DisplayName("returns 401 without auth")
    void requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/pws/orders/export")
                        .param("tab", "all")
                        .param("userId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("returns XLSX content-type header")
    void returnsXlsxContentType() throws Exception {
        when(orderHistoryService.exportExcel("all", 1L))
                .thenReturn(new byte[]{0x50, 0x4B}); // minimal fake bytes

        mockMvc.perform(get("/api/v1/pws/orders/export")
                        .param("tab", "all")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                        org.hamcrest.Matchers.containsString(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")));
    }

    @Test
    @DisplayName("Content-Disposition header includes expected filename pattern")
    void contentDispositionIncludesFilename() throws Exception {
        when(orderHistoryService.exportExcel("inProcess", 1L))
                .thenReturn(new byte[]{0x50, 0x4B});

        mockMvc.perform(get("/api/v1/pws/orders/export")
                        .param("tab", "inProcess")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("PWS OrderHistory_")));
    }

    @Test
    @DisplayName("returns 400 when service throws for unknown tab")
    void returns400ForUnknownTab() throws Exception {
        when(orderHistoryService.exportExcel("bogus", 1L))
                .thenThrow(new IllegalArgumentException("Unknown tab: bogus"));

        mockMvc.perform(get("/api/v1/pws/orders/export")
                        .param("tab", "bogus")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isBadRequest());
    }
}
```

---

## Edge Case Summary

The following edge cases must be covered by at least one test each. Use this as a pre-commit checklist.

| Edge Case | Covered In | Test Method |
|-----------|-----------|-------------|
| `userId = null` | `OrderHistoryServiceTest` | `ListOrders.nullUserId_throws` |
| `tab = null` or unknown | `OrderHistoryServiceTest` | `ListOrders.unknownTab_throwsIllegalArgumentException` |
| Empty buyer code list | `OrderHistoryServiceTest` | `ListOrders.noBuyerCodes_returnsEmptyPage` |
| `offerId` with no offer items | `OrderHistoryServiceTest` | `GetDetailsBySku.emptyOffer_returnsEmptyList` |
| `orderId` not found | `OrderHistoryServiceTest` | `GetDetailsByDevice.orderNotFound_returnsEmptyList` |
| `null` JSON content in order | `OrderHistoryServiceTest` | `GetDetailsByDevice.nullJsonContent_returnsEmptyList` |
| `null` shipped qty on offer item | `OrderHistoryServiceTest` | `GetDetailsBySku.nullShippedQty_mapsToZero` |
| Declined/Canceled strikethrough | `OrderHistoryResponseTest` | `strikethrough_declined/canceled` |
| Null status (no strikethrough) | `OrderHistoryResponseTest` | `noStrikethrough_nullStatus` |
| Draft offer excluded from VIEW | `OrderHistoryViewRepositoryIT` | `excludesDraftOffers` |
| Offer-only row has negative ID | `OrderHistoryViewRepositoryIT` | `rowIdNegativeForOfferOnly` |
| Export: empty data file is valid | `OrderHistoryServiceTest` | `ExportExcel.emptyData_producesHeaderOnlyFile` |
| Export: correct 13-column headers | `OrderHistoryServiceTest` | `ExportExcel.firstRowIsHeaderRow` |
| Export: large dataset performance | `OrderHistoryServiceTest` | `ExportExcel.largeDataset_producesFileWithinTimeout` |
| No auth on any endpoint | `OrderHistoryControllerTest` | Every `requiresAuth` test |

---

## File Locations Summary

| Test File | Phase | Purpose |
|-----------|-------|---------|
| `backend/src/test/java/com/ecoatm/salesplatform/service/OrderHistoryServiceTest.java` | 1, 3, 4 | Unit — service layer including export |
| `backend/src/test/java/com/ecoatm/salesplatform/controller/OrderHistoryControllerTest.java` | 1, 3, 4 | Web layer — all endpoints |
| `backend/src/test/java/com/ecoatm/salesplatform/repository/OrderHistoryViewRepositoryIT.java` | 1 | Integration — VIEW SQL and Specifications |
| `backend/src/test/java/com/ecoatm/salesplatform/dto/OrderHistoryResponseTest.java` | 3 | Unit — strikethrough derived field |

---

## TDD Cycle Checklist Per Phase

### Phase 1

- [ ] Write `OrderHistoryServiceTest` — all `ListOrders` and `GetTabCounts` tests (they will fail: `OrderHistoryService` does not exist)
- [ ] Run `mvn test -pl backend -Dtest=OrderHistoryServiceTest` — confirm RED
- [ ] Create `OrderHistoryView` entity, `OrderHistoryViewRepository`, `OrderHistoryTabCounts` record, `OrderHistoryResponse` record
- [ ] Implement `OrderHistoryService.listOrders()` and `getTabCounts()`
- [ ] Run tests — confirm GREEN
- [ ] Write `OrderHistoryControllerTest` — all `ListOrders` and `GetTabCounts` controller tests
- [ ] Run — confirm RED
- [ ] Implement `OrderHistoryController` with both endpoints
- [ ] Run — confirm GREEN
- [ ] Write `OrderHistoryViewRepositoryIT` — VIEW correctness tests
- [ ] Run with real Postgres via Testcontainers — confirm RED (VIEW does not exist yet)
- [ ] Apply `V37__order_history_view.sql` via Flyway
- [ ] Run — confirm GREEN
- [ ] Run `mvn test -pl backend` — full suite green
- [ ] Run `mvn jacoco:report -pl backend` — verify coverage delta

### Phase 3

- [ ] Write `GetDetailsBySku` and `GetDetailsByDevice` tests in `OrderHistoryServiceTest` — confirm RED
- [ ] Implement `getDetailsBySku()` and `getDetailsByDevice()`
- [ ] Write `OrderHistoryResponseTest` strikethrough tests — confirm RED
- [ ] Add `isStrikethrough()` to `OrderHistoryResponse`
- [ ] Write controller detail tests — confirm RED
- [ ] Add detail endpoints to controller
- [ ] Full suite green

### Phase 4

- [ ] Add `poi-ooxml` to `pom.xml`
- [ ] Write `ExportExcel` tests in `OrderHistoryServiceTest` — confirm RED (method does not exist)
- [ ] Implement `exportExcel()` in service
- [ ] Write export controller tests — confirm RED
- [ ] Add export endpoint to controller
- [ ] Full suite green
- [ ] Verify `mvn jacoco:report` shows 80%+ coverage for `OrderHistoryService` and `OrderHistoryController`
