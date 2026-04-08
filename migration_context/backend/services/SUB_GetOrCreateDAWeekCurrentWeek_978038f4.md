# Microflow Analysis: SUB_GetOrCreateDAWeekCurrentWeek

### Execution Steps:
1. **Search the Database for **AuctionUI.Auction** using filter: { Show everything } (Call this list **$Auction**)**
2. **Retrieve
      - Store the result in a new variable called **$Week****
3. **Search the Database for **EcoATM_DA.DAWeek** using filter: { [EcoATM_DA.DAWeek_Week = $Week]
 } (Call this list **$DAWeek**)**
4. **Decision:** "DAWeek exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
