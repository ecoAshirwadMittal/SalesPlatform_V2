# Microflow Analysis: SUB_UploadPOToSnowFlake

### Requirements (Inputs):
- **$PODetailList** (A record of type: EcoATM_PO.PODetail)
- **$FromWeekNumber** (A record of type: Object)
- **$ToWeekNumber** (A record of type: Object)
- **$FromYearNumber** (A record of type: Object)
- **$ToYearNumber** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **Administration.Account** using filter: { [id = $currentUser]
 } (Call this list **$Account**)**
2. **Create Variable**
3. **Export Xml**
4. **Execute Database Query
      - Store the result in a new variable called **$DEV_UPLOADPURCHASEORDER**** ⚠️ *(This step has a safety catch if it fails)*
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
