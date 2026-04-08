# Microflow Analysis: SUB_BatchRequest_Send

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$BatchRequest** (A record of type: MicrosoftGraph.Batch)

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
8. **Retrieve
      - Store the result in a new variable called **$Responses****
9. **Retrieve
      - Store the result in a new variable called **$BatchResponseList****
10. **Log Message**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
