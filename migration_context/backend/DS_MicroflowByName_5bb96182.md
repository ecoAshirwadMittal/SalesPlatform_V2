# Microflow Analysis: DS_MicroflowByName

### Requirements (Inputs):
- **$CompleteName** (A record of type: Object)
- **$ParametersAllowed** (A record of type: Object)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$MicroflowList****
2. **Aggregate List
      - Store the result in a new variable called **$Count****
3. **Decision:** "= 1"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **=0**
4. **Take the list **$MicroflowList**, perform a [Head], and call the result **$Microflow****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
