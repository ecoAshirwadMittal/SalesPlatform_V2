# Microflow Analysis: ACT_SchedulingAuction_Save_Admin

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Permanently save **$undefined** to the database.**
2. **Retrieve
      - Store the result in a new variable called **$Auction****
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
5. **Close Form**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
