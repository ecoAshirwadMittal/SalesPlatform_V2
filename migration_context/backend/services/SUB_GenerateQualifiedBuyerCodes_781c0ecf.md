# Microflow Analysis: SUB_GenerateQualifiedBuyerCodes

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BuyerCodeList_Qualified** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Run another process: "AuctionUI.SUB_ClearQualifiedBuyerList"**
5. **Run another process: "EcoATM_BuyerManagement.SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe"
      - Store the result in a new variable called **$SpecialBuyerCodeList****
6. **Take the list **$BuyerCodeList_Qualified**, perform a [Subtract], and call the result **$BuyerCodeList_FinalQualified****
7. **Run another process: "AuctionUI.SUB_CreateQualifiedBuyersEntity"**
8. **Run another process: "AuctionUI.SUB_CreateQualifiedBuyersEntity"**
9. **Take the list **$BuyerCodeList_Qualified**, perform a [Union], and call the result **$BuyerCodeList_AllQualifedAndSPT****
10. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    BuyerCodeType = 'Data_Wipe'
    or BuyerCodeType = 'Wholesale'
  )
  and
  (
    EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'
  )
] } (Call this list **$BuyerCodeList_All_Active**)**
11. **Take the list **$BuyerCodeList_All_Active**, perform a [Subtract], and call the result **$BuyerCodeList_NotQualified****
12. **Run another process: "AuctionUI.SUB_CreateQualifiedBuyersEntity"**
13. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_QualifiedBuyers] to: "$BuyerCodeList_AllQualifedAndSPT"
      - **Save:** This change will be saved to the database immediately.**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
