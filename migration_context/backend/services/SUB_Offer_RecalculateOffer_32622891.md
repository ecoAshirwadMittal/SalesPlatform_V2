# Microflow Analysis: SUB_Offer_RecalculateOffer

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
2. **Run another process: "EcoATM_PWS.VAL_Offer_Finalize"
      - Store the result in a new variable called **$IsFinalizeValid****
3. **Decision:** "$IsFinalizeValid"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "EcoATM_PWS.VAL_Offer_Counter"
      - Store the result in a new variable called **$isValid****
5. **Decision:** "$isValid"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Run another process: "EcoATM_PWS.SUB_CalculateCounterOfferSummary"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
