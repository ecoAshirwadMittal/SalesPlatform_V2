# Microflow Analysis: VAL_OfferBuyerExcelFile_IsValid

### Requirements (Inputs):
- **$OfferExcelImportDocument** (A record of type: EcoATM_PWS.ManageFileDocument)

### Execution Steps:
1. **Decision:** "exist?"
   - If [true] -> Move to: **hasContent?**
   - If [false] -> Move to: **Finish**
2. **Decision:** "hasContent?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
