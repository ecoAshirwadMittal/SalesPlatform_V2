# Microflow Analysis: SUB_CreateQualifiedBuyersEntity

### Requirements (Inputs):
- **$BuyerCodeList** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$isSpecialTreatmentBuyer** (A record of type: Object)
- **$enum_BuyerCodeQualificationType** (A record of type: Object)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_QualifiedBuyers] to: "$BuyerCodeList
"
      - **Save:** This change will be saved to the database immediately.**
2. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    BuyerCodeType = 'Data_Wipe'
    or BuyerCodeType = 'Wholesale'
  )
  and
  (
    EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'
  )
] } (Call this list **$BuyerCodeList_All_Active**)**
3. **Retrieve
      - Store the result in a new variable called **$QualifiedBuyerCodesList_Existing****
4. **Take the list **$BuyerCodeList_All_Active**, perform a [Subtract], and call the result **$BuyerCodeList_NotQualified****
5. **Create List
      - Store the result in a new variable called **$QualifiedBuyerCodesList****
6. **Run another process: "AuctionUI.SUB_ClearQualifiedBuyerList"**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
