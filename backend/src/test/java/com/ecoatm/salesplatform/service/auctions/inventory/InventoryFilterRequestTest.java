package com.ecoatm.salesplatform.service.auctions.inventory;

import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterOp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryFilterRequestTest {

    @Nested
    @DisplayName("new wire format (op,value)")
    class NewWireFormat {

        @Test
        @DisplayName("contains,A_YYY → CONTAINS + A_YYY (ignores legacy mode)")
        void contains_value() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("contains,A_YYY", null);
            assertThat(r.active()).isTrue();
            assertThat(r.op()).isEqualTo(FilterOp.CONTAINS);
            assertThat(r.value()).isEqualTo("A_YYY");
        }

        @Test
        @DisplayName("eq,73 → EQ + 73")
        void eq_value() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("eq,73", null);
            assertThat(r.op()).isEqualTo(FilterOp.EQ);
            assertThat(r.value()).isEqualTo("73");
        }

        @Test
        @DisplayName("startsWith,A_ → STARTS_WITH + A_")
        void startsWith_value() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("startsWith,A_", null);
            assertThat(r.op()).isEqualTo(FilterOp.STARTS_WITH);
            assertThat(r.value()).isEqualTo("A_");
        }

        @Test
        @DisplayName("endsWith,iPhone → ENDS_WITH + iPhone")
        void endsWith_value() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("endsWith,iPhone", null);
            assertThat(r.op()).isEqualTo(FilterOp.ENDS_WITH);
            assertThat(r.value()).isEqualTo("iPhone");
        }

        @Test
        @DisplayName("gte,100 → GTE + 100")
        void gte_value() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("gte,100", null);
            assertThat(r.op()).isEqualTo(FilterOp.GTE);
            assertThat(r.value()).isEqualTo("100");
        }

        @Test
        @DisplayName("empty → EMPTY (valueless, no comma needed)")
        void empty_valueless() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("empty", null);
            assertThat(r.op()).isEqualTo(FilterOp.EMPTY);
            assertThat(r.value()).isNull();
        }

        @Test
        @DisplayName("notEmpty → NOT_EMPTY (valueless)")
        void notEmpty_valueless() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("notEmpty", null);
            assertThat(r.op()).isEqualTo(FilterOp.NOT_EMPTY);
            assertThat(r.value()).isNull();
        }

        @Test
        @DisplayName("contains, with empty value after the comma → no-op (ignored)")
        void contains_blankValue_ignored() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("contains,", null);
            assertThat(r.active()).isFalse();
        }
    }

    @Nested
    @DisplayName("legacy bare-value format")
    class LegacyFormat {

        @Test
        @DisplayName("bare value with mode=null defaults to CONTAINS")
        void bareValue_defaultsToContains() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("Apple", null);
            assertThat(r.op()).isEqualTo(FilterOp.CONTAINS);
            assertThat(r.value()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("bare value with mode=equals → EQ")
        void bareValue_modeEquals() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("A_YYY", "equals");
            assertThat(r.op()).isEqualTo(FilterOp.EQ);
            assertThat(r.value()).isEqualTo("A_YYY");
        }

        @Test
        @DisplayName("bare value with mode=contains (explicit) → CONTAINS")
        void bareValue_modeContainsExplicit() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("A_", "contains");
            assertThat(r.op()).isEqualTo(FilterOp.CONTAINS);
        }

        @Test
        @DisplayName("bare value with mode=EQUALS (mixed case) → EQ")
        void bareValue_modeMixedCase() {
            InventoryFilterRequest r = InventoryFilterRequest.parse("A_YYY", "EQUALS");
            assertThat(r.op()).isEqualTo(FilterOp.EQ);
        }
    }

    @Nested
    @DisplayName("inactive cases")
    class Inactive {

        @Test
        @DisplayName("null raw → none()")
        void nullRaw() {
            InventoryFilterRequest r = InventoryFilterRequest.parse(null, null);
            assertThat(r.active()).isFalse();
            assertThat(r.op()).isNull();
        }

        @Test
        @DisplayName("blank raw → none()")
        void blankRaw() {
            assertThat(InventoryFilterRequest.parse("", null).active()).isFalse();
            assertThat(InventoryFilterRequest.parse("   ", null).active()).isFalse();
        }
    }

    @Nested
    @DisplayName("ambiguous values")
    class Ambiguous {

        @Test
        @DisplayName("value with comma but unknown op token → treated as legacy bare value")
        void unknownOpPrefix() {
            // "Hello,World" — "Hello" is not a valid FilterOp token, so the
            // whole thing is a legacy contains-search for "Hello,World".
            InventoryFilterRequest r = InventoryFilterRequest.parse("Hello,World", null);
            assertThat(r.op()).isEqualTo(FilterOp.CONTAINS);
            assertThat(r.value()).isEqualTo("Hello,World");
        }
    }
}
