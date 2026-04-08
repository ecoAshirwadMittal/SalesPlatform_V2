# Microflow Analysis: SUB_Subscription_Delete

### Requirements (Inputs):
- **$Subscription** (A record of type: MicrosoftGraph.Subscription)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Run another process: "MicrosoftGraph.DELETE"
      - Store the result in a new variable called **$Response****
5. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Sub microflow**
6. **Log Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
