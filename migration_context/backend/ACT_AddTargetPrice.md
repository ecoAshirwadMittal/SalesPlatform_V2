# Microflow Detailed Specification: ACT_AddTargetPrice

### 📥 Inputs (Parameters)
- **$BidRoundSelectionFilter** (Type: AuctionUI.BidRoundSelectionFilter)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.TargetPriceFactor** (Result: **$NewTargetPriceFactor**)
      - Set **FactorType** = `AuctionUI.ENUM_TargetPriceFactorType.Percentage_Factor`
      - Set **TargetPriceFactor_BidRoundSelectionFilter** = `$BidRoundSelectionFilter`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.