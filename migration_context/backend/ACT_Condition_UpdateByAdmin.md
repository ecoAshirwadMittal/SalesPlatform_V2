# Microflow Detailed Specification: ACT_Condition_UpdateByAdmin

### 📥 Inputs (Parameters)
- **$Condition** (Type: EcoATM_PWSMDM.Condition)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
3. **Commit/Save **$Condition** to Database**
4. **Close current page/popup**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.