# Microflow Analysis: ACT_CarryForwardBids

### Requirements (Inputs):
- **$CarryOverBidsNP** (A record of type: AuctionUI.CarryOverBidsNP)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Java Action Call
      - Store the result in a new variable called **$Variable**** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
