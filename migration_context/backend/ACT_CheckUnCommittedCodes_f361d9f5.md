# Microflow Analysis: ACT_CheckUnCommittedCodes

### Requirements (Inputs):
- **$NewBuyerHelper** (A record of type: EcoATM_BuyerManagement.NewBuyerHelper)

### Execution Steps:
1. **Decision:** "dw empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
2. **Create Object
      - Store the result in a new variable called **$NewNewBuyerCodeHelper_DW****
3. **Run another process: "AuctionUI.ACT_CreateNewBuyerCodeHelper"
      - Store the result in a new variable called **$DW_isValid****
4. **Decision:** "valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
5. **Decision:** "PO empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
6. **Create Object
      - Store the result in a new variable called **$NewNewBuyerCodeHelper_PO****
7. **Run another process: "AuctionUI.ACT_CreateNewBuyerCodeHelper"
      - Store the result in a new variable called **$PO_isValid****
8. **Decision:** "valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
9. **Decision:** "WH empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
10. **Create Object
      - Store the result in a new variable called **$NewNewBuyerCodeHelper_WH****
11. **Run another process: "AuctionUI.ACT_CreateNewBuyerCodeHelper"
      - Store the result in a new variable called **$WH_isValid****
12. **Decision:** "valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
