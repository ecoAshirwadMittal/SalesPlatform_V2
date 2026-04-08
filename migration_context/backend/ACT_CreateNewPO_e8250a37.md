# Microflow Analysis: ACT_CreateNewPO

### Execution Steps:
1. **Run another process: "EcoATM_PO.SUB_GetOrCreatePOHelper"
      - Store the result in a new variable called **$POHelper****
2. **Create Object
      - Store the result in a new variable called **$NewPurchaseOrder****
3. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.POHelper.ENUM_ActionType] to: "EcoATM_PO.ENUM_POActionType._New
"
      - Change [EcoATM_PO.POHelper.MissingBuyerCodeValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.InvalidFileValidation] to: "false
"
      - Change [EcoATM_PO.POHelper.InValidPOPeriod] to: "false
"
      - Change [EcoATM_PO.POHelper_PurchaseOrder] to: "$NewPurchaseOrder
"**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.PurchaseOrder_PurchaseOrderDoc] to: "$NewPurchaseOrder/EcoATM_PO.PurchaseOrder_PurchaseOrderDoc"**
5. **Show Page**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
