# Microflow Analysis: ACT_SendWeekDataToSnowflake

### Execution Steps:
1. **Search the Database for **EcoATM_MDM.Week** using filter: { Show everything } (Call this list **$WeekList**)**
2. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
3. **Execute Database Query
      - Store the result in a new variable called **$AUCTIONS_UPSERT_WEEK_DATA**** ⚠️ *(This step has a safety catch if it fails)*
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
