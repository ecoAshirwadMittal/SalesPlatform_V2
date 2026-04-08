# Microflow Analysis: SUB_FetchPWSSearchObject

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$PWSSearchList****
2. **Decision:** "search avl?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Take the list **$PWSSearchList**, perform a [Sort], and call the result **$NewPWSSearchList****
4. **Take the list **$NewPWSSearchList**, perform a [Head], and call the result **$LatestSearch****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
