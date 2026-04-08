# Microflow Detailed Specification: ACT_PWSErroConfig_Procedeed

### 📥 Inputs (Parameters)
- **$ManageFileDocument** (Type: EcoATM_PWS.ManageFileDocument)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$ManageFileDocument/HasContents`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. **Call Microflow **EcoATM_PWSIntegration.SUB_Oracle_ErrorMessage** (Result: **$IsSuccess**)**
      3. **Close current page/popup**
      4. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `if($IsSuccess) then 'Successfully imported' else 'Import failed'`
      - Set **Message** = `if($IsSuccess) then 'File ['+$ManageFileDocument/Name+'] imported.' else 'Unable to import the selected file'`
      - Set **CSSClass** = `if($IsSuccess) then 'pws-file-upload-success' else 'pws-file-upload-error'`
      - Set **IsSuccess** = `$IsSuccess`**
      5. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.