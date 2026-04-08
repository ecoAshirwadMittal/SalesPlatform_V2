# Microflow Analysis: SUB_UploadCohortMapping

### Requirements (Inputs):
- **$CohortMappingDoc** (A record of type: EcoATM_Reports.CohortMappingDoc)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Import Excel Data
      - Store the result in a new variable called **$CohortMappingFileImportList****
5. **Run another process: "EcoATM_Reports.SUB_DeleteCohortMappingData"**
6. **Create List
      - Store the result in a new variable called **$NewCohortMappingList****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Show Page**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
