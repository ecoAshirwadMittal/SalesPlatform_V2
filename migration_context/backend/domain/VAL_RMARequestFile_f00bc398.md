# Microflow Analysis: VAL_RMARequestFile

### Requirements (Inputs):
- **$RMARequest_ImportHelperList** (A record of type: EcoATM_RMA.RMARequest_ImportHelper)
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$RMAFile** (A record of type: EcoATM_RMA.RMAFile)

### Execution Steps:
1. **Create Variable**
2. **Create List
      - Store the result in a new variable called **$RMAItemList****
3. **Create List
      - Store the result in a new variable called **$DeviceList****
4. **Search the Database for **EcoATM_RMA.RMAReasons** using filter: { [
  (
    IsActive = true()
  )
] } (Call this list **$RMAReasonsList**)**
5. **Create Object
      - Store the result in a new variable called **$NewInvalidRMAItem****
6. **Create List
      - Store the result in a new variable called **$InvalidIMEI_ExportHelperList****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAFile.IsValid] to: "$IsValidRMA"**
9. **Decision:** "Is Valid RMA?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Aggregate List
      - Store the result in a new variable called **$RequestSalesTotal****
11. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.RequestSKUs] to: "length($DeviceList)"
      - Change [EcoATM_RMA.RMA.RequestSalesTotal] to: "$RequestSalesTotal"
      - Change [EcoATM_RMA.RMA.RequestQty] to: "length($RMAItemList)"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
