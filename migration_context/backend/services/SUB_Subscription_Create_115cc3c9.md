# Microflow Analysis: SUB_Subscription_Create

### Requirements (Inputs):
- **$Subscription** (A record of type: MicrosoftGraph.Subscription)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Export Xml**
5. **Run another process: "MicrosoftGraph.POST"
      - Store the result in a new variable called **$Response****
6. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Sub microflow**
7. **Import Xml**
8. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Subscription._Id] to: "$Subscription_Created/_Id"
      - Change [MicrosoftGraph.Subscription_Authorization] to: "$Authorization"
      - **Save:** This change will be saved to the database immediately.**
9. **Delete**
10. **Log Message**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
