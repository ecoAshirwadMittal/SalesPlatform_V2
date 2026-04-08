# Microflow Analysis: ACT_GetOrCreateReserveBidSync

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimeZoneOffset****
2. **Search the Database for **EcoATM_EB.ReserveBidSync** using filter: { Show everything } (Call this list **$ExistingReserveBidSync**)**
3. **Decision:** "ReserveBidSync exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_EB.ReserveBidSync.LastSyncDateTime] to: "subtractHours([%CurrentDateTime%],$TimeZoneOffset)
"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
