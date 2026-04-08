# Microflow Detailed Specification: SUB_UploadCohortMapping

### 📥 Inputs (Parameters)
- **$CohortMappingDoc** (Type: EcoATM_Reports.CohortMappingDoc)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'UploadCohortMapping'`**
2. **Create Variable **$Description** = `'Upload Cohort Mapping Data'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **ImportExcelData**
5. **Call Microflow **EcoATM_Reports.SUB_DeleteCohortMappingData****
6. **CreateList**
7. 🔄 **LOOP:** For each **$IteratorCohortMappingFileImport** in **$CohortMappingFileImportList**
   │ 1. **Create **EcoATM_Reports.CohortMapping** (Result: **$NewCohortMapping**)
      - Set **CurrentEcoID** = `floor($IteratorCohortMappingFileImport/Current_EcoID)`
      - Set **CurrentModelName** = `$IteratorCohortMappingFileImport/Current_Model_Name`
      - Set **CohortEcoID** = `floor($IteratorCohortMappingFileImport/Cohort_EcoID)`
      - Set **CohortModelName** = `$IteratorCohortMappingFileImport/Current_Model_Name`
      - Set **AvgSellingPrice** = `$IteratorCohortMappingFileImport/ASP`**
   │ 2. **Add **$$NewCohortMapping** to/from list **$NewCohortMappingList****
   └─ **End Loop**
8. **Commit/Save **$NewCohortMappingList** to Database**
9. **Maps to Page: **EcoATM_Reports.PG_CohortMapping****
10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.