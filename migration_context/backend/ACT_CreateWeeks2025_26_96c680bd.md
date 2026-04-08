# Microflow Analysis: ACT_CreateWeeks2025_26

### Execution Steps:
1. **Log Message**
2. **Search the Database for **EcoATM_MDM.Week** using filter: { [Year=2025 or Year=2026]
 } (Call this list **$WeekList**)**
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
19. **Create Variable**
20. **Log Message**
21. **Create Variable**
22. **Create Variable**
23. **Create Variable**
24. **Create Variable**
25. **Create Object
      - Store the result in a new variable called **$NewWeek2026****
26. **Create Variable**
27. **Create Variable**
28. **Change Variable**
29. **Change Variable**
30. **Change Variable**
31. **Create Object
      - Store the result in a new variable called **$NewWeek_12026****
32. **Decision:** "done?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
33. **Log Message**
34. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
