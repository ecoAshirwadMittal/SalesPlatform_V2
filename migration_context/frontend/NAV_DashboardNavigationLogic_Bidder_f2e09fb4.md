# Microflow Analysis: NAV_DashboardNavigationLogic_Bidder

### Execution Steps:
1. **Close Form**
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
5. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [
  (
    Name = $currentUser/Name
  )
] } (Call this list **$EcoATMDirectUser**)**
6. **Search the Database for **EcoATM_BuyerManagement.AuctionsFeature** using filter: { Show everything } (Call this list **$AuctionsFeature**)**
7. **Run another process: "AuctionUI.SUB_HasWholesaleBuyerCodesAssigned"
      - Store the result in a new variable called **$HasWholesaleBuyerCode****
8. **Decision:** "User Agreement Acknowledged?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Run another process: "EcoATM_UserManagement.SUB_SendUserLoginToSnowflake"**
10. **Run another process: "AuctionUI.ACT_CreateBuyerCodeSelectHelper"**
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
