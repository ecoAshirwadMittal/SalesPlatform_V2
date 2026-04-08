# Microflow Analysis: BCO_LogBuyerCodeChange

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [Code = $BuyerCode/Code] } (Call this list **$OriginalBuyerCode**)**
5. **Decision:** "is a change?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Decision:** "Type Changed?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Create Object
      - Store the result in a new variable called **$BuyerCodeTypeChangeLog****
9. **Decision:** "Logical Delete?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
10. **Create Object
      - Store the result in a new variable called **$BuyerCodeSoftDeleteLog****
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
