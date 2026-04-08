# Microflow Analysis: ACT_Delete_AuctionFromAdmin

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Week****
2. **Delete**
3. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
4. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
