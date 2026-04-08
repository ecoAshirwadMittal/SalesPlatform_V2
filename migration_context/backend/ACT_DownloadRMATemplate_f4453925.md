# Microflow Analysis: ACT_DownloadRMATemplate

### Execution Steps:
1. **Search the Database for **EcoATM_RMA.RMATemplate** using filter: { [IsActive] } (Call this list **$RMATemplate**)**
2. **Decision:** "Template Available?"
   - If [true] -> Move to: **Name =Today?**
   - If [false] -> Move to: **Finish**
3. **Decision:** "Name =Today?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Download File**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
