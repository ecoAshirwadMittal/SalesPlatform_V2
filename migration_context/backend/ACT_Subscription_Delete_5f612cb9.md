# Microflow Analysis: ACT_Subscription_Delete

### Requirements (Inputs):
- **$Subscription** (A record of type: MicrosoftGraph.Subscription)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Subscription_Delete"
      - Store the result in a new variable called **$SubscriptionDeleted****
2. **Decision:** "Check if "$SubscriptionDeleted" is true"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Delete**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
