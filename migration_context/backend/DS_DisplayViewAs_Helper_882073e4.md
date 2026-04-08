# Microflow Analysis: DS_DisplayViewAs_Helper

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GetCurrentUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
2. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
3. **Take the list **$UserRoleList**, perform a [Find] where: { 'Bidder' }, and call the result **$BidderUserRoleExists****
4. **Decision:** "is logged in user a bidder?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser = $currentUser]
[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active']
[BuyerCodeType='Premium_Wholesale'] } (Call this list **$BuyerCodeList**)**
6. **Decision:** "More then on BuyerCode?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Create Object
      - Store the result in a new variable called **$NewUiHelper_1****
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
