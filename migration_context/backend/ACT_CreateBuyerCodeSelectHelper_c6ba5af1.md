# Microflow Analysis: ACT_CreateBuyerCodeSelectHelper

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Run another process: "AuctionUI.ACT_GetMostRecentAuction"
      - Store the result in a new variable called **$Auction****
3. **Run another process: "AuctionUI.ACT_GetActiveSchedulingAuction"
      - Store the result in a new variable called **$CurrentStartedRound****
4. **Run another process: "AuctionUI.ACT_HandleSingleBuyerCodeLogin"
      - Store the result in a new variable called **$SingleBuyerCodeNotAuction****
5. **Decision:** "true?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { [(EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser/Name = $currentUser/Name and Status = 'Active')] } (Call this list **$BuyerList**)**
7. **Search the Database for **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** using filter: { Show everything } (Call this list **$BuyerCodeSelect_HelperList**)**
8. **Delete**
9. **Search the Database for **System.UserRole** using filter: { [System.UserRoles = $currentUser and (Name='SalesOps' or Name='Administrator')] } (Call this list **$SalesOpsUserRole**)**
10. **Create Object
      - Store the result in a new variable called **$NewParent_BuyerCodeSelectHelperNP****
11. **Create List
      - Store the result in a new variable called **$BuyerSelect_HelperList_NP****
12. **Decision:** "Not SalesOps"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
13. **Run another process: "Custom_Logging.SUB_Log_Info"**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Run another process: "AuctionUI.SUB_CreateBuyerCodeSelectHelper"
      - Store the result in a new variable called **$BuyerCodeSelect_HelperList_2****
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
