# Microflow Analysis: SUB_GetBidQuantityValueForImportedBidData

### Requirements (Inputs):
- **$BidDataImport_Round3** (A record of type: AuctionUI.BidDataImport_Round3)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Decision:** "NO QUANTITY LIMIT?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
