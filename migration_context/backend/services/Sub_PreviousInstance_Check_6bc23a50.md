# Microflow Analysis: Sub_PreviousInstance_Check

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.PreviousInstance** using filter: { Show everything } (Call this list **$PreviousInstanceList**)**
2. **Decision:** "Found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Aggregate List
      - Store the result in a new variable called **$CountPrevious****
4. **Search the Database for **System.XASInstance** using filter: { Show everything } (Call this list **$XASInstanceList**)**
5. **Aggregate List
      - Store the result in a new variable called **$CountCurrent****
6. **Create Variable**
7. **Create Variable**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Decision:** "no changes?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
