# Microflow Analysis: ACT_CreateBuyerCodeSelectHelper_2

### Execution Steps:
1. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [(Status = 'Started')] } (Call this list **$CurrentStartedRound**)**
2. **Search the Database for **AuctionUI.Buyer** using filter: { [(AuctionUI.EcoATMDirectUser_Buyer/AuctionUI.EcoATMDirectUser/Name = $currentUser/Name and Status = 'Active')] } (Call this list **$BuyerList**)**
3. **Create List
      - Store the result in a new variable called **$BuyerSelect_HelperList****
4. **Search the Database for **System.UserRole** using filter: { [System.UserRoles = $currentUser and (Name='SalesOps' or Name='Administrator')] } (Call this list **$SalesOpsUserRole**)**
5. **Decision:** "Not SalesOps"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
