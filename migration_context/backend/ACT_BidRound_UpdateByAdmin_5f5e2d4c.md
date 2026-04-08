# Microflow Analysis: ACT_BidRound_UpdateByAdmin

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$JSONContent****
2. **Run another process: "Custom_Logging.SUB_Log_Warning"**
3. **Permanently save **$undefined** to the database.**
4. **Close Form**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
