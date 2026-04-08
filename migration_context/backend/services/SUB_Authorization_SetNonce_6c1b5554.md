# Microflow Analysis: SUB_Authorization_SetNonce

### Requirements (Inputs):
- **$Scope** (A record of type: Object)

### Execution Steps:
1. **Decision:** "Scope includes ID token?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Java Action Call
      - Store the result in a new variable called **$RandomHash****
3. **Create Variable**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
