# Microflow Analysis: SUB_GetSchedulingAuctionEndDate

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Week****
2. **Retrieve
      - Store the result in a new variable called **$Auction****
3. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [HasRound]
[AuctionUI.SchedulingAuction_Auction=$Auction] } (Call this list **$SchedulingAuction**)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [DateTime] result.
