# Microflow Analysis: ACT_BidData_SelectImportSheet_TEST

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Log Message**
2. **Close Form**
3. **Retrieve
      - Store the result in a new variable called **$BidRound****
4. **Search the Database for **AuctionUI.BidData_ImportSettings** using filter: { Show everything } (Call this list **$BidData_ImportSettings**)**
5. **Decision:** "Import Settings?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$BidImport_DefaultTemplate****
7. **Create Object
      - Store the result in a new variable called **$NewXMLDocumentTemplate****
8. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidRound_XMLDocumentTemplate] to: "$NewXMLDocumentTemplate"**
9. **Create Object
      - Store the result in a new variable called **$NewBidUploadPageHelper****
10. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
11. **Show Page**
12. **Log Message**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
