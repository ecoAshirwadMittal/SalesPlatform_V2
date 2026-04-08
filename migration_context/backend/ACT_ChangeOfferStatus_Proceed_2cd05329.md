# Microflow Analysis: ACT_ChangeOfferStatus_Proceed

### Requirements (Inputs):
- **$ChangeOfferStatusHelper** (A record of type: EcoATM_PWS.ChangeOfferStatusHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "EcoATM_PWS.VAL_ChargeOfferStatusHelper_IsValid"
      - Store the result in a new variable called **$IsValid****
3. **Decision:** "IsValid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "EcoATM_PWS.SUB_ChangeOfferStatus_GetOrderList"
      - Store the result in a new variable called **$OrderList****
5. **Decision:** "exist?"
   - If [true] -> Move to: **Not change status?**
   - If [false] -> Move to: **Activity**
6. **Decision:** "Not change status?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Between dates?**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Aggregate List
      - Store the result in a new variable called **$Count****
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Close Form**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
