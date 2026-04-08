# Microflow Analysis: SUB_Transaction_End_StartNew

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$IsInTransactionBeforeEnd****
2. **Decision:** "Is In Transaction?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
4. **Java Action Call
      - Store the result in a new variable called **$IsInTransactionBeforeStart****
5. **Decision:** "Is In Transaction?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
6. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
