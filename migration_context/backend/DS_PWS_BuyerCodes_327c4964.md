# Microflow Analysis: DS_PWS_BuyerCodes

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "AuctionUI.ACT_GetCurrentUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
5. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
6. **Take the list **$UserRoleList**, perform a [Find] where: { 'Bidder' }, and call the result **$BidderUserRoleExists****
7. **Create List
      - Store the result in a new variable called **$BuyerCodeList****
8. **Decision:** "is logged in user a bidder?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
9. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [Status='Active']
[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active']
[BuyerCodeType = 'Premium_Wholesale']
 } (Call this list **$AllActiveBuyerCodeList**)**
10. **Change List**
11. **Create Object
      - Store the result in a new variable called **$NewParent_NPBuyerCodeSelectHelper****
12. **Create List
      - Store the result in a new variable called **$NP_BuyerCodeSelect_HelperList_R1R3****
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
