# Microflow Analysis: Act_GetOrCreateBuyerCodeSubmitConfig

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
2. **Search the Database for **EcoATM_BuyerManagement.AuctionsFeature** using filter: { Show everything } (Call this list **$BuyerCodeSubmitConfig**)**
3. **Decision:** "Not Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Object
      - Store the result in a new variable called **$NewBuyerCodeSubmitConfig****
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
