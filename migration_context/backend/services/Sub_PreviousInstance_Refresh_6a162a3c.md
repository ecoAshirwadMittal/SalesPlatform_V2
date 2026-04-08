# Microflow Analysis: Sub_PreviousInstance_Refresh

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.PreviousInstance** using filter: { Show everything } (Call this list **$PreviousInstanceList_Delete**)**
2. **Delete**
3. **Create List
      - Store the result in a new variable called **$PreviousInstanceList****
4. **Search the Database for **System.XASInstance** using filter: { Show everything } (Call this list **$XASInstanceList**)**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
