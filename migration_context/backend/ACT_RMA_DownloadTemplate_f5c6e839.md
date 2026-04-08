# Microflow Analysis: ACT_RMA_DownloadTemplate

### Execution Steps:
1. **Search the Database for **EcoATM_RMA.RMATemplate** using filter: { [IsActive]
 } (Call this list **$LastRMATemplate**)**
2. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Download File**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
