# Microflow Detailed Specification: ACT_SetRoundStartDefaults

### 📥 Inputs (Parameters)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. **Update **$SchedulingAuction_Helper** (and Save to DB)
      - Set **Round2_Start_DateTime** = `addMinutes($SchedulingAuction_Helper/Round1_End_DateTime,$BuyerCodeSubmitConfig/AuctionRound2MinutesOffset)`
      - Set **Round3_Start_DateTime** = `addMinutes($SchedulingAuction_Helper/Round2_End_DateTime,$BuyerCodeSubmitConfig/AuctionRound3MinutesOffset)`**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.