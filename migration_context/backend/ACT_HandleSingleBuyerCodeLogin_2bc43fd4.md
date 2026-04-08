# Microflow Analysis: ACT_HandleSingleBuyerCodeLogin

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Decision:** "ScheduleAuction Empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer[EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser/Name = $currentUser/Name]] } (Call this list **$BuyerCodeList**)**
3. **Aggregate List
      - Store the result in a new variable called **$BuyerCodeCount****
4. **Retrieve
      - Store the result in a new variable called **$NP_BuyerCodeSelect_HelperList****
5. **Take the list **$NP_BuyerCodeSelect_HelperList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser/Name= $currentUser/Name }, and call the result **$Filter_BuyerCodesCurrentUser****
6. **Aggregate List
      - Store the result in a new variable called **$CountNP_BuyerCodeSelect****
7. **Search the Database for **Administration.Account** using filter: { [id = $currentUser] } (Call this list **$AccountList**)**
8. **Decision:** "count = 1"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
9. **Show Page**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
