# Microflow Analysis: CAL_BuyerOfferItem_CSSStyle

### Requirements (Inputs):
- **$BuyerOfferItem** (A record of type: EcoATM_PWS.BuyerOfferItem)

### Execution Steps:
1. **Decision:** "Offer price 
NOT empty?"
   - If [true] -> Move to: **Quantity 
Not empty and >0**
   - If [false] -> Move to: **Finish**
2. **Decision:** "Quantity 
Not empty and >0"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Retrieve
      - Store the result in a new variable called **$Device****
4. **Decision:** "Device
CurrentList Price 
Not empty"
   - If [true] -> Move to: **OfferPrice < CurrentListPrice**
   - If [false] -> Move to: **Finish**
5. **Decision:** "OfferPrice < CurrentListPrice"
   - If [false] -> Move to: **OfferPrice < CurrentListPrice**
   - If [true] -> Move to: **Finish**
6. **Decision:** "OfferPrice < CurrentListPrice"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
