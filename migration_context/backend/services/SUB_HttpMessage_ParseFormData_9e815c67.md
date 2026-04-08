# Microflow Analysis: SUB_HttpMessage_ParseFormData

### Requirements (Inputs):
- **$Key** (A record of type: Object)
- **$HttpRequest** (A record of type: System.HttpRequest)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$ParametersList****
2. **Take the list **$ParametersList**, perform a [FindByExpression] where: { startsWith($currentObject/Value,$Key) }, and call the result **$NewSplitItem****
3. **Create Variable**
4. **Java Action Call
      - Store the result in a new variable called **$ParametersList_KeyValue****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Decision:** "Check if "Content" contains Key"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
