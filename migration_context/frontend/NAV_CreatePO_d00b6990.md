# Microflow Analysis: NAV_CreatePO

### Execution Steps:
1. **Run another process: "EcoATM_PO.SUB_GetOrCreatePOHelper"
      - Store the result in a new variable called **$POHelper****
2. **Search the Database for **EcoATM_PO.PurchaseOrder** using filter: { Show everything } (Call this list **$PurchaseOrder**)**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_PO.POHelper_PurchaseOrder] to: "$PurchaseOrder
"**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
