# Microflow Analysis: ACT_Offer_BuyerAcceptAllCounters

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$ObjectInfo****
2. **Decision:** "isLocked?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
