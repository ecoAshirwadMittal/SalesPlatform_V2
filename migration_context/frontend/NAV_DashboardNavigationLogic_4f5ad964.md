# Microflow Analysis: NAV_DashboardNavigationLogic

### Execution Steps:
1. **Close Form**
2. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [
  (
    Name = $currentUser/Name
  )
] } (Call this list **$EcoATMDirectUser**)**
3. **Run another process: "EcoATM_UserManagement.SUB_SendUserLoginToSnowflake"**
4. **Decision:** "Landing Page Pref"
   - If [Premium_Wholesale] -> Move to: **Activity**
   - If [Wholesale_Auction] -> Move to: **Finish**
   - If [(empty)] -> Move to: **Finish**
5. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
6. **Take the list **$UserRoleList**, perform a [Find] where: { 'Bidder' }, and call the result **$BidderUserRoleExists****
7. **Decision:** "is logged in user a bidder?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Submicroflow**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
