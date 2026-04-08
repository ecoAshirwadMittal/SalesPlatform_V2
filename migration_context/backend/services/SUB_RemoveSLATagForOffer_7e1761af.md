# Microflow Analysis: SUB_RemoveSLATagForOffer

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferBeyondSLA] to: "false"
      - **Save:** This change will be saved to the database immediately.**
3. **Show Message**
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
