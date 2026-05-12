package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ActionRecommendation;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit tests for {@link ActionRecommendationService}.
 *
 * <p>Targets 100% branch coverage on the §9 decision tree. Each test
 * isolates one branch by constructing a line whose other rules cannot
 * fire — e.g. price-floor cases use a model match within the same
 * allowed brand so the diff classifier returns {@code UNCLASSIFIED} and
 * the price rule actually executes.
 */
class ActionRecommendationServiceTest {

    private ActionRecommendationService service;

    @BeforeEach
    void setUp() {
        service = new ActionRecommendationService();
    }

    /**
     * Builder helper — Wrong-Device line entity has many fields and we
     * only set the ones the recommender reads.
     */
    private WrongDeviceLine newLine() {
        return new WrongDeviceLine();
    }

    @Test
    @DisplayName("Apple iPhone with model diff -> ACCEPT")
    void appleModelDiff_recommendsAccept() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 11");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        assertThat(result).isEqualTo(ActionRecommendation.ACCEPT);
    }

    @Test
    @DisplayName("Samsung Galaxy with grade diff -> DECLINE")
    void samsungGradeDiff_recommendsDecline() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Samsung");
        line.setActualBrand("Samsung");
        line.setExpectedModel("Galaxy S21");
        line.setActualModel("Galaxy S21");
        line.setExpectedGrade("A");
        line.setActualGrade("B");
        line.setExpectedAmountPaid(new BigDecimal("80.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("60.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Motorola with capacity diff (encoded in model string) -> ACCEPT")
    void motorolaCapacityDiff_recommendsAccept() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Motorola");
        line.setActualBrand("Motorola");
        line.setExpectedModel("Moto G Power 128GB");
        line.setActualModel("Moto G Power 64GB");
        line.setExpectedGrade("B");
        line.setActualGrade("B");
        line.setExpectedAmountPaid(new BigDecimal("90.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("50.00"));

        assertThat(result).isEqualTo(ActionRecommendation.ACCEPT);
    }

    @Test
    @DisplayName("Off-allowlist brand (LG) -> DECLINE regardless of diff")
    void offAllowlistBrand_alwaysDeclines() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("LG");
        line.setActualBrand("LG");
        // Set up a model diff so the rule that would otherwise Accept can't fire.
        line.setExpectedModel("Velvet 5G");
        line.setActualModel("Wing 5G");
        line.setExpectedGrade("A");
        line.setActualGrade("A");

        ActionRecommendation result = service.recommend(line, new BigDecimal("30.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Apple 'iPhone 12 No Power' -> DECLINE even with capacity diff")
    void appleNoPower_recommendsDeclineEvenWithCapacityDiff() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12 128GB No Power");
        line.setActualModel("iPhone 12 256GB");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Apple no-power via description field (no_power token) -> DECLINE")
    void appleNoPower_descriptionTokenVariant_recommendsDecline() {
        // Exercises the no_power token + the expectedDeviceDescription branch
        // of isNoPower (vs. expectedModel in the previous test).
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12 128GB");
        line.setExpectedDeviceDescription("iPhone 12 NoPower 128GB");
        line.setActualModel("iPhone 12 256GB");
        line.setExpectedGrade("A");
        line.setActualGrade("A");

        ActionRecommendation result = service.recommend(line, null);

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Apple with no diff but received worth > paid -> DECLINE per price floor")
    void appleReceivedWorthMoreThanPaid_recommendsDecline() {
        // Same brand + same model + same grade so the diff classifier
        // returns UNCLASSIFIED — that lets the price-floor rule fire.
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 12");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("40.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("75.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Null expectedBrand -> DECLINE")
    void nullExpectedBrand_recommendsDecline() {
        WrongDeviceLine line = newLine();
        // expectedBrand left null — every downstream rule should bypass.
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 11");

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Null expectedAmountPaid -> does not crash, default DECLINE")
    void nullExpectedAmountPaid_doesNotCrash() {
        // No diff fires, no price-floor data — should fall through to
        // the safe-default DECLINE without NPE.
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 12");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        // expectedAmountPaid left null

        ActionRecommendation result = service.recommend(line, new BigDecimal("50.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Null receivedLatestPrice -> does not crash, default DECLINE when no other rule fires")
    void nullReceivedLatestPrice_doesNotCrash() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 12");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        ActionRecommendation result = service.recommend(line, null);

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Brand match is case-insensitive (lowercase 'apple' allowlisted)")
    void lowercaseBrand_stillAllowlisted() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("apple"); // lowercase
        line.setActualBrand("apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 11");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        // Same as the canonical-case Apple case — allowlist should hit
        // and the model diff routes to ACCEPT.
        assertThat(result).isEqualTo(ActionRecommendation.ACCEPT);
    }

    @Test
    @DisplayName("Grade diff dominates capacity diff (both differ) -> DECLINE")
    void gradeDiffWinsOverModelDiff_recommendsDecline() {
        // Exercises the rule-priority guarantee: grade-or-color wins
        // even when capacity-or-model would otherwise have classified.
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12 128GB");
        line.setActualModel("iPhone 12 256GB");
        line.setExpectedGrade("A");
        line.setActualGrade("B");

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Different brands -> diff classifier returns UNCLASSIFIED")
    void differentBrandsButGradeMatch_fallsThroughToPriceFloor() {
        // Expected Apple (allowlisted), Actual Samsung — model differs
        // but brands differ too, so CAPACITY_OR_MODEL rule (which gates
        // on sameBrand) does not fire. Grade matches, so GRADE_OR_COLOR
        // does not fire either. Falls through to the price-floor rule.
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Samsung");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("Galaxy S21");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        // receivedLatestPrice < expectedAmountPaid -> price-floor doesn't
        // fire, default DECLINE.
        ActionRecommendation result = service.recommend(line, new BigDecimal("60.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }

    @Test
    @DisplayName("Null actualGrade does not crash the diff classifier")
    void nullActualGrade_doesNotCrash() {
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 11");
        line.setExpectedGrade("A");
        // actualGrade left null — the null-safe diff helper must not NPE
        // and must not register a grade diff.
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        // Grade diff didn't fire (null-safe), so capacity/model takes
        // over: same brand + different model -> ACCEPT.
        assertThat(result).isEqualTo(ActionRecommendation.ACCEPT);
    }

    @Test
    @DisplayName("Null expectedGrade with non-null actualGrade -> no grade diff, fall through")
    void nullExpectedGrade_doesNotTripGradeDiff() {
        // Exercises the a == null branch of notEqualNullSafe (the previous
        // null-actualGrade test hit b == null).
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        line.setActualBrand("Apple");
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 11");
        // expectedGrade left null; actualGrade present
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        // Null grade should not trip a grade diff, so the model diff
        // wins and the line auto-Accepts.
        assertThat(result).isEqualTo(ActionRecommendation.ACCEPT);
    }

    @Test
    @DisplayName("Null actualBrand prevents capacity/model diff classification")
    void nullActualBrand_blocksCapacityOrModelClassification() {
        // Exercises sameBrand()'s actual == null branch. Without an actual
        // brand we cannot prove the model diff is within the same brand,
        // so capacity-or-model does not fire; falls through to default DECLINE.
        WrongDeviceLine line = newLine();
        line.setExpectedBrand("Apple");
        // actualBrand left null
        line.setExpectedModel("iPhone 12");
        line.setActualModel("iPhone 11");
        line.setExpectedGrade("A");
        line.setActualGrade("A");
        line.setExpectedAmountPaid(new BigDecimal("100.00"));

        ActionRecommendation result = service.recommend(line, new BigDecimal("40.00"));

        assertThat(result).isEqualTo(ActionRecommendation.DECLINE);
    }
}
