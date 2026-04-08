# Microflow Analysis: SUB_DeleteCohortMappingData

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Search the Database for **EcoATM_Reports.CohortMapping** using filter: { Show everything } (Call this list **$CohortMappingList**)**
5. **Aggregate List
      - Store the result in a new variable called **$TotalItems****
6. **Decision:** "not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Create Variable**
8. **Create Variable**
9. **Create Variable**
10. **Java Action Call
      - Store the result in a new variable called **$Variable****
11. **Search the Database for **EcoATM_Reports.CohortMapping** using filter: { Show everything } (Call this list **$CohortMappingBatchList**)**
12. **Aggregate List
      - Store the result in a new variable called **$RetrievedCount****
13. **Change Variable**
14. **Delete**
15. **Java Action Call
      - Store the result in a new variable called **$Variable_2****
16. **Log Message**
17. **Decision:** "end of list?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
18. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
