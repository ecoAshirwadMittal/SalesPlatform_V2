# Microflow Analysis: ACT_GetBuyerCodes_SalesRep_2

### Execution Steps:
1. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [RoundStatus='Started'] } (Call this list **$StartedSchedulingAuction**)**
2. **Decision:** "Round 2?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Search the Database for **AuctionUI.BuyerCode** using filter: { [AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active'] } (Call this list **$AllActiveBuyerCodeList**)**
4. **Create Object
      - Store the result in a new variable called **$NewParent_NPBuyerCodeSelectHelper****
5. **Create List
      - Store the result in a new variable called **$NP_BuyerCodeSelect_HelperList_R1R3****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
