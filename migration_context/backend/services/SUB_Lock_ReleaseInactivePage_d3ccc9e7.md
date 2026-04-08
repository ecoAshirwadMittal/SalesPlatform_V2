# Microflow Analysis: SUB_Lock_ReleaseInactivePage

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Create Variable**
3. **Search the Database for **EcoATM_Lock.Lock** using filter: { [Active]
[(LockUpdatedOn!=empty and LockUpdatedOn< $maxInactivityTime)
or (LockUpdatedOn=empty and LockedOn < $maxInactivityTime)]
 } (Call this list **$LockList**)**
4. **Decision:** "exist?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
