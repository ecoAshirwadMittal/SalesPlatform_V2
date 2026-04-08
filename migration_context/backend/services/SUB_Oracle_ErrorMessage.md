# Microflow Detailed Specification: SUB_Oracle_ErrorMessage

### 📥 Inputs (Parameters)
- **$DataContent** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSIntegration.PWSResponseConfig**  (Result: **$OriginalPWSResponseConfigList**)**
2. **ImportXml**
3. **CreateList**
4. **CreateList**
5. 🔄 **LOOP:** For each **$IteratorPWSResponseConfig** in **$NewPWSResponseConfigList**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/SourceSystem=$IteratorPWSResponseConfig/SourceSystem and $currentObject/SourceErrorCode=$IteratorPWSResponseConfig/SourceErrorCode` (Result: **$foundWSResponseConfig**)**
   │ 2. 🔀 **DECISION:** `$foundWSResponseConfig!=empty`
   │    ➔ **If [true]:**
   │       1. **Add **$$foundWSResponseConfig
** to/from list **$DeletePWSResponseConfigList****
   │       2. **Add **$$IteratorPWSResponseConfig
** to/from list **$SavePWSResponseConfigList****
   │    ➔ **If [false]:**
   │       1. **Add **$$IteratorPWSResponseConfig
** to/from list **$SavePWSResponseConfigList****
   └─ **End Loop**
6. **Commit/Save **$SavePWSResponseConfigList** to Database**
7. **Delete**
8. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.