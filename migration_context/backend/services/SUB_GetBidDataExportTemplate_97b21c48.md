# Microflow Analysis: SUB_GetBidDataExportTemplate

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Decision:** "Round 1?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
2. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='BidDataExport']
 } (Call this list **$MxTemplate**)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
