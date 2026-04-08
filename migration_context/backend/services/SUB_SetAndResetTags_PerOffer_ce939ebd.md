# Microflow Analysis: SUB_SetAndResetTags_PerOffer

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "EcoATM_PWS.SUB_SetSameSKUTag"**
3. **Decision:** "eligible to reset tags?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.SameSKUOffer] to: "false"
      - **Save:** This change will be saved to the database immediately.**
5. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
