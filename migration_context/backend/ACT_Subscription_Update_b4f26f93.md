# Microflow Analysis: ACT_Subscription_Update

### Requirements (Inputs):
- **$Subscription** (A record of type: MicrosoftGraph.Subscription)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Authorization_GetActive"
      - Store the result in a new variable called **$Authorization****
2. **Run another process: "MicrosoftGraph.SUB_Subscription_Renew"
      - Store the result in a new variable called **$Updated****
3. **Decision:** "Updated?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Close Form**
5. **Update the **$undefined** (Object):**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
