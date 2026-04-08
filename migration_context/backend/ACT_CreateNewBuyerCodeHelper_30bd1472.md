# Microflow Analysis: ACT_CreateNewBuyerCodeHelper

### Requirements (Inputs):
- **$BuyerCode_Helper** (A record of type: EcoATM_BuyerManagement.BuyerCode_Helper)
- **$NewBuyerHelper** (A record of type: EcoATM_BuyerManagement.NewBuyerHelper)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.NewBuyerHelper.Code_DW] to: "toUpperCase($NewBuyerHelper/Code_DW)"
      - Change [EcoATM_BuyerManagement.NewBuyerHelper.Code_PO] to: "toUpperCase($NewBuyerHelper/Code_PO)"
      - Change [EcoATM_BuyerManagement.NewBuyerHelper.Code_WH] to: "toUpperCase($NewBuyerHelper/Code_WH)"
      - **Save:** This change will be saved to the database immediately.**
2. **Create Variable**
3. **Decision:** "DW?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **PO?**
4. **Change Variable**
5. **Run another process: "AuctionUI.VAL_ValidateBuyerCode_PreSave"
      - Store the result in a new variable called **$isValid****
6. **Decision:** "Valid?"
   - If [true] -> Move to: **DW?**
   - If [false] -> Move to: **Finish**
7. **Decision:** "DW?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **PO?**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.NewBuyerHelper.Code_DW] to: "empty"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
