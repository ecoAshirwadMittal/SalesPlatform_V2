# Microflow Analysis: BDe_MxXpath

### Requirements (Inputs):
- **$MxXPath** (A record of type: XLSReport.MxXPath)

### Execution Steps:
1. **Decision:** "Found child"
   - If [true] -> Move to: **Retrieve over parent the child**
   - If [false] -> Move to: **Finish**
2. **Retrieve
      - Store the result in a new variable called **$ChildXPath****
3. **Delete**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
