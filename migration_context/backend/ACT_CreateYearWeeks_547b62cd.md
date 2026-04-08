# Microflow Analysis: ACT_CreateYearWeeks

### Execution Steps:
1. **Log Message**
2. **Search the Database for **EcoATM_MDM.Week** using filter: { Show everything } (Call this list **$WeekList**)**
3. **Delete**
4. **Log Message**
5. **Create Variable**
6. **Create Variable**
7. **Create Variable**
8. **Create Variable**
9. **Create Variable**
10. **Create Object
      - Store the result in a new variable called **$NewWeek****
11. **Create Variable**
12. **Create Variable**
13. **Change Variable**
14. **Change Variable**
15. **Change Variable**
16. **Create Object
      - Store the result in a new variable called **$NewWeek_1****
17. **Decision:** "done?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
18. **Log Message**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
