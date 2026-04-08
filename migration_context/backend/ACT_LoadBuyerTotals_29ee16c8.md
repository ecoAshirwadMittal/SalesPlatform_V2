# Microflow Analysis: ACT_LoadBuyerTotals

### Requirements (Inputs):
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'
  )
] } (Call this list **$ActiveBuyerCodeList**)**
2. **Aggregate List
      - Store the result in a new variable called **$AllBuyerCodeCount****
3. **Take the list **$ActiveBuyerCodeList**, perform a [Filter] where: { AuctionUI.enum_BuyerCodeType.Data_Wipe }, and call the result **$BuyerCodeList_DW****
4. **Aggregate List
      - Store the result in a new variable called **$DWBuyerCount****
5. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_Helper.BuyersTotal] to: "$AllBuyerCodeCount"
      - Change [AuctionUI.SchedulingAuction_Helper.BuyersDWOnly] to: "$DWBuyerCount"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
