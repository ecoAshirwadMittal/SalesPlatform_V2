# Microflow Analysis: ACT_Buyer_EditSave

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Run another process: "AuctionUI.Val_CheckDisablingBuyer"
      - Store the result in a new variable called **$BuyerCodeValidation****
2. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Run another process: "EcoATM_MDM.SUB_Buyer_Save"
      - Store the result in a new variable called **$Variable****
4. **Decision:** "Valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
