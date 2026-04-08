# Microflow Analysis: DS_GerOrCreateAggregateInventoryHelper

### Execution Steps:
1. **Search the Database for **Administration.Account** using filter: { [id = $currentUser]
 } (Call this list **$Account**)**
2. **Retrieve
      - Store the result in a new variable called **$AgreegateInventoryHelper****
3. **Decision:** "AgreegateInventoryHelper exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
