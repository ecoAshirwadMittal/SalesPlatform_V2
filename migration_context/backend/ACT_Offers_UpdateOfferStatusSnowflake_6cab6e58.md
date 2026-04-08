# Microflow Analysis: ACT_Offers_UpdateOfferStatusSnowflake

### Requirements (Inputs):
- **$UpdateSnowflakeHelper** (A record of type: EcoATM_PWS.UpdateSnowflakeHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Create Variable**
5. **Create Variable**
6. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferSubmissionDate>= $FromDate
and
OfferSubmissionDate<=$ToDate] } (Call this list **$OfferList**)**
7. **Decision:** "List not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Close Form**
11. **Show Message**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
