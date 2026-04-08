# Microflow Analysis: ACT_GetBuyerCodes_SalesRep_Back

### Execution Steps:
1. **Search the Database for **AuctionUI.EcoATMDirectUser** using filter: { [Name=$currentUser/Name] } (Call this list **$EcoATMDirectUserList**)**
2. **Retrieve
      - Store the result in a new variable called **$NP_BuyerCodeSelect_HelperList_Check****
3. **Take the list **$NP_BuyerCodeSelect_HelperList_Check**, perform a [Head], and call the result **$NewNP_BuyerCodeSelect_Helper_3****
4. **Retrieve
      - Store the result in a new variable called **$Parent_NPBuyerCodeSelectHelper****
5. **Create Object
      - Store the result in a new variable called **$NewNP_BuyerCodeSelect_Helper_2_1_1****
6. **Decision:** "Is NP empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
7. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [RoundStatus='Started'] } (Call this list **$StartedSchedulingAuction**)**
8. **Decision:** "Round 2?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
9. **Search the Database for **AuctionUI.BuyerCode** using filter: { [AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active'] } (Call this list **$AllActiveBuyerCodeList**)**
10. **Create Object
      - Store the result in a new variable called **$NewParent_NPBuyerCodeSelectHelper****
11. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
12. **Create Object
      - Store the result in a new variable called **$NewNP_BuyerCodeSelect_Helper_2****
13. **Show Page**
14. **Create List
      - Store the result in a new variable called **$NP_BuyerCodeSelect_HelperList****
15. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
