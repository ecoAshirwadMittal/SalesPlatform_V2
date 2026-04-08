# Microflow Analysis: ACT_GetWeekList

### Execution Steps:
1. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$CurrentWeek****
2. **Search the Database for **AuctionUI.Week** using filter: { Show everything } (Call this list **$WeekList**)**
3. **Create List
      - Store the result in a new variable called **$Week_Picker_HelperList****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
