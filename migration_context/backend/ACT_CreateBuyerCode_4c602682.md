# Microflow Analysis: ACT_CreateBuyerCode

### Requirements (Inputs):
- **$BuyerCode_Helper** (A record of type: EcoATM_BuyerManagement.BuyerCode_Helper)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.BuyerCode_Helper.Code] to: "toUpperCase($BuyerCode_Helper/Code)"**
2. **Run another process: "AuctionUI.VAL_ValidateBuyerCode"
      - Store the result in a new variable called **$isValid****
3. **Decision:** "valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.Status] to: "AuctionUI.enum_BuyerStatus.Active"
      - **Save:** This change will be saved to the database immediately.**
5. **Permanently save **$undefined** to the database.**
6. **Create Object
      - Store the result in a new variable called **$NewBuyerCode****
7. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList****
8. **Change List**
9. **Permanently save **$undefined** to the database.**
10. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.BuyerCode_Helper.Code] to: "empty"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
