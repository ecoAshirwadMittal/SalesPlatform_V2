# Microflow Analysis: SUB_ShowPWSOffersPage

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Decision:** "OfferStatus?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Java Action Call
      - Store the result in a new variable called **$OfferObjectId****
3. **Search the Database for **EcoATM_Lock.Lock** using filter: { [ObjectID = $OfferObjectId and LockedBy = $currentUser/Name]  } (Call this list **$Lock**)**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_Lock.Lock.Active] to: "false"
      - **Save:** This change will be saved to the database immediately.**
5. **Run another process: "EcoATM_PWS.SUB_SetAndResetTags"**
6. **Show Page**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
