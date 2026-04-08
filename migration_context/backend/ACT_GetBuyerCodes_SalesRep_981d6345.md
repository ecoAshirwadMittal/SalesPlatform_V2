# Microflow Analysis: ACT_GetBuyerCodes_SalesRep

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [RoundStatus='Started'] } (Call this list **$StartedSchedulingAuction**)**
3. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'
    and (BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale')
  )
] } (Call this list **$AllActiveBuyerCodeList**)**
4. **Create Object
      - Store the result in a new variable called **$NewParent_NPBuyerCodeSelectHelper****
5. **Create List
      - Store the result in a new variable called **$NP_BuyerCodeSelect_HelperList_R1R3****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
