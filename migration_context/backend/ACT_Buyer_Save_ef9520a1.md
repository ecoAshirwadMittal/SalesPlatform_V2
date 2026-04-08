# Microflow Analysis: ACT_Buyer_Save

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Run another process: "EcoATM_MDM.SUB_Buyer_Save"
      - Store the result in a new variable called **$Valid****
2. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Close Form**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
