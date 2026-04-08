# Microflow Analysis: ACT_Subscription_Create

### Requirements (Inputs):
- **$Subscription** (A record of type: MicrosoftGraph.Subscription)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Authorization_GetActive"
      - Store the result in a new variable called **$Authorization****
2. **Run another process: "MicrosoftGraph.SUB_Subscription_Create"
      - Store the result in a new variable called **$Subscribed****
3. **Decision:** "Check if "$Subscription/_Id" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Permanently save **$undefined** to the database.**
5. **Close Form**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
