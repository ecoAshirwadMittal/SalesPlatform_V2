# Microflow Analysis: ACT_Delete_SchedulingAuctionFromAdmin

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Auction****
2. **Retrieve
      - Store the result in a new variable called **$Week****
3. **Delete**
4. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
