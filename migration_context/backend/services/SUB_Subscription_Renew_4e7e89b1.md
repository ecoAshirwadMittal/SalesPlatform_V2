# Microflow Analysis: SUB_Subscription_Renew

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$Subscription** (A record of type: MicrosoftGraph.Subscription)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Export Xml**
5. **Run another process: "MicrosoftGraph.PATCH"
      - Store the result in a new variable called **$Response****
6. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Sub microflow**
7. **Permanently save **$undefined** to the database.**
8. **Log Message**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
