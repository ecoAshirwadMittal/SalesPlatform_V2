# Microflow Analysis: ACT_CreateBuyerCodeSelectHelper_PopUp

### Execution Steps:
1. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [(RoundStatus = 'Started')] } (Call this list **$CurrentStartedRound**)**
2. **Retrieve
      - Store the result in a new variable called **$NP_BuyerCodeSelect_HelperList****
3. **Decision:** "List empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Search the Database for **AuctionUI.Buyer** using filter: { [(AuctionUI.EcoATMDirectUser_Buyer/AuctionUI.EcoATMDirectUser/Name = $currentUser/Name and Status = 'Active')] } (Call this list **$BuyerList**)**
5. **Search the Database for **System.UserRole** using filter: { [System.UserRoles = $currentUser and (Name='SalesOps' or Name='Administrator')] } (Call this list **$SalesOpsUserRole**)**
6. **Decision:** "Round 2?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
7. **Create List
      - Store the result in a new variable called **$BuyerSelect_HelperList****
8. **Decision:** "Not SalesOps"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Permanently save **$undefined** to the database.**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
