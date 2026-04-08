# Microflow Detailed Specification: SUB_Configuration_ExecuteAction

### 📥 Inputs (Parameters)
- **$Environment** (Type: Custom_Logging.Environment)
- **$ConfigurationList** (Type: Custom_Logging.Configuration)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$ConfigurationList!=empty`
   ➔ **If [true]:**
      1. **CreateList**
      2. 🔄 **LOOP:** For each **$IteratorConfiguration** in **$ConfigurationList**
         │ 1. **Call Microflow **Custom_Logging.SUB_Configuration_GetResourceLocation** (Result: **$targetFile**)**
         │ 2. **Call Microflow **Custom_Logging.SUB_Log_Info****
         │ 3. **JavaCallAction**
         │ 4. 🔀 **DECISION:** `trim($Checksum)!=''`
         │    ➔ **If [true]:**
         │       1. **Call Microflow **Custom_Logging.SUB_ConfigurationByFIle_GetOrCreate** (Result: **$ConfigurationByFile**)**
         │       2. 🔀 **DECISION:** `$ConfigurationByFile/MD5!=$Checksum`
         │          ➔ **If [true]:**
         │             1. **Call Microflow **Custom_Logging.SUB_ConfigurationFile_ImportData****
         │             2. **Update **$ConfigurationByFile**
      - Set **MD5** = `$Checksum`
      - Set **LastImportedDate** = `[%CurrentDateTime%]`**
         │             3. **Add **$$ConfigurationByFile
** to/from list **$ConfigurationByFileList****
         │          ➔ **If [false]:**
         │             1. **Call Microflow **Custom_Logging.SUB_Log_Info****
         │    ➔ **If [false]:**
         │       1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
         └─ **End Loop**
      3. **Commit/Save **$ConfigurationByFileList** to Database**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.