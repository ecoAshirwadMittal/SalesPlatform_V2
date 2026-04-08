# Microflow Analysis: ACT_Device_SetItemTypeToPWS

### Execution Steps:
1. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [ItemType= '' or ItemType=empty]

 } (Call this list **$DeviceList_noItemType**)**
2. **Decision:** "not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create List
      - Store the result in a new variable called **$DeviceList****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Aggregate List
      - Store the result in a new variable called **$Count****
7. **Show Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
