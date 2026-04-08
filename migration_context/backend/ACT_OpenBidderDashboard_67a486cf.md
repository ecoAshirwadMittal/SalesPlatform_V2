# Microflow Analysis: ACT_OpenBidderDashboard

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
2. **Search the Database for **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** using filter: { Show everything } (Call this list **$BuyerCodeSelect_HelperList**)**
3. **Delete**
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { Show everything } (Call this list **$SchedulingAuctionList**)**
6. **Decision:** "No auction"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
7. **Java Action Call
      - Store the result in a new variable called **$Variable****
8. **Close Form**
9. **Show Page**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
