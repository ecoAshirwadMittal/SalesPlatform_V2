# Microflow Analysis: ACT_RefrshEBPrice_backup

### Execution Steps:
1. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$Week****
2. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimeOffset****
3. **Run another process: "EcoATM_EB.ACT_EB_Sync_Get_Monday_Date"
      - Store the result in a new variable called **$mondaydate****
4. **Execute Database Query
      - Store the result in a new variable called **$EBPriceList**** ⚠️ *(This step has a safety catch if it fails)*
5. **Decision:** "EBPriceList has items?"
   - If [undefined] -> Move to: **Activity**
   - If [undefined] -> Move to: **Activity**
6. **Create List
      - Store the result in a new variable called **$ReserveBidList****
7. **Search the Database for **EcoATM_EB.ReserveBid** using filter: { Show everything } (Call this list **$ExistingReserveBid_CurrentWeek_1**)**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Permanently save **$undefined** to the database.**
10. **Run another process: "EcoATM_EB.ACT_GetOrCreateReserveBidSync"
      - Store the result in a new variable called **$ReserveBidSync****
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
