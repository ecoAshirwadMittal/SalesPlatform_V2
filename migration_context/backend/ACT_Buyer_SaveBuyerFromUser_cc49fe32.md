# Microflow Analysis: ACT_Buyer_SaveBuyerFromUser

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)
- **$NewBuyerHelper** (A record of type: EcoATM_BuyerManagement.NewBuyerHelper)

### Execution Steps:
1. **Run another process: "EcoATM_MDM.SUB_Buyer_Save"
      - Store the result in a new variable called **$Valid****
2. **Decision:** "valid?"
   - If [true] -> Move to: **New Buyer helper not empty?**
   - If [false] -> Move to: **Finish**
3. **Decision:** "New Buyer helper not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Retrieve
      - Store the result in a new variable called **$EcoATMDirectUser****
5. **Decision:** "user not exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Close Form**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
