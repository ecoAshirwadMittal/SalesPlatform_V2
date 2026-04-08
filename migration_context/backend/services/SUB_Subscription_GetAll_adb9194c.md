# Microflow Analysis: SUB_Subscription_GetAll

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "MicrosoftGraph.GET"
      - Store the result in a new variable called **$Response****
3. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Import Xml**
5. **Aggregate List
      - Store the result in a new variable called **$Count****
6. **Log Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
