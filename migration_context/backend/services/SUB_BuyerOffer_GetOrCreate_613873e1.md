# Microflow Analysis: SUB_BuyerOffer_GetOrCreate

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Decision:** "exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Search the Database for **EcoATM_PWS.BuyerOffer** using filter: { [EcoATM_PWS.BuyerOffer_BuyerCode=$BuyerCode]
 } (Call this list **$BuyerOffer**)**
3. **Decision:** "found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
