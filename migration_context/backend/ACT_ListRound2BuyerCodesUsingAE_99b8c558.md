# Microflow Analysis: ACT_ListRound2BuyerCodesUsingAE

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Search the Database for **AuctionUI.BidRoundSelectionFilter** using filter: { [
  (
    Round = 2
  )
] } (Call this list **$BidRoundSelectionFilter**)**
5. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    BuyerCodeType = 'Wholesale'
    or BuyerCodeType = 'Data_Wipe'
  )
  and
  (
    EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'
  )
] } (Call this list **$BuyerCodeList**)**
6. **Create List
      - Store the result in a new variable called **$BuyerCodeList_Qualified****
7. **Decision:** "! all buyer codes"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
