# Microflow Analysis: VAL_Create_Auction

### Requirements (Inputs):
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Decision:** "Auction_Name != ''?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Variable**
5. **Create Variable**
6. **Search the Database for **AuctionUI.Auction** using filter: { Show everything } (Call this list **$Auction**)**
7. **Create Variable**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Decision:** "Title unique?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
10. **Change Variable**
11. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction_Helper.Name_Unique] to: "false"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
