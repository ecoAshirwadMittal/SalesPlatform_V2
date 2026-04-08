# Microflow Analysis: ACT_CreateBuyerCodeSelectHelper_2

### Execution Steps:
1. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [(Status = 'Started')] } (Call this list **$CurrentStartedRound**)**
2. **Run another process: "AuctionUI.ACT_HandleSingleBuyerCodeLogin"
      - Store the result in a new variable called **$SingleBuyerCodeNotAuction****
3. **Decision:** "true?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
4. **Search the Database for **AuctionUI.Buyer** using filter: { [(AuctionUI.EcoATMDirectUser_Buyer/AuctionUI.EcoATMDirectUser/Name = $currentUser/Name and Status = 'Active')] } (Call this list **$BuyerList**)**
5. **Search the Database for **AuctionUI.BuyerCodeSelect_Helper** using filter: { Show everything } (Call this list **$BuyerCodeSelect_HelperList**)**
6. **Delete**
7. **Search the Database for **System.UserRole** using filter: { [System.UserRoles = $currentUser and (Name='SalesOps' or Name='Administrator')] } (Call this list **$SalesOpsUserRole**)**
8. **Create Object
      - Store the result in a new variable called **$NewParent_BuyerCodeSelectHelperNP****
9. **Decision:** "Round 2?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Run another process: "AuctionUI.ACT_Round2BuyerCodes"
      - Store the result in a new variable called **$BuyerCodeList_Round2****
11. **Decision:** "List not empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
12. **Create List
      - Store the result in a new variable called **$BuyerSelect_HelperList_Round2_NP****
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Run another process: "AuctionUI.SUB_CreateBuyerCodeSelectHelper"
      - Store the result in a new variable called **$BuyerCodeSelect_HelperList****
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
