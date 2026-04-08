# Microflow Analysis: SUB_Auction_DeleteByAdmin

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$Week****
3. **Java Action Call
      - Store the result in a new variable called **$JSONContent****
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Delete**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
8. **Run another process: "Custom_Logging.SUB_Log_Info"**
9. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
10. **Run another process: "Custom_Logging.SUB_Log_Info"**
11. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
12. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
