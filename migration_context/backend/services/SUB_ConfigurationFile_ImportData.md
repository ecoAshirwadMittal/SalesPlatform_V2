# Microflow Detailed Specification: SUB_ConfigurationFile_ImportData

### 📥 Inputs (Parameters)
- **$FileToImport** (Type: Variable)
- **$MicroflowToExecute** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **JavaCallAction**
3. **JavaCallAction**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.