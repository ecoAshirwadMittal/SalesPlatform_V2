# Microflow Analysis: SUB_GetBidValueForImportedBidData

### Requirements (Inputs):
- **$BidDataImport_Round3** (A record of type: AuctionUI.BidDataImport_Round3)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Decision:** "Y?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Variable** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Decimal] result.
