# Microflow Detailed Specification: SUB_UploadPOToSnowFlake

### 📥 Inputs (Parameters)
- **$PODetailList** (Type: EcoATM_PO.PODetail)
- **$FromWeekNumber** (Type: Variable)
- **$ToWeekNumber** (Type: Variable)
- **$FromYearNumber** (Type: Variable)
- **$ToYearNumber** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
2. **Create Variable **$CreatedBy** = `if($Account !=empty) then $Account/Email else 'Scheduler'`**
3. **ExportXml**
4. **ExecuteDatabaseQuery**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.