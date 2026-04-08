# Microflow Analysis: SUB_Offer_ConvertToJSON

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
4. **Create List
      - Store the result in a new variable called **$DeviceSFTempList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
7. **Delete**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
