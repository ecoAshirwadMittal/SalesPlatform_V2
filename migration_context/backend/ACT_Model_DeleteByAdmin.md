# Microflow Detailed Specification: ACT_Model_DeleteByAdmin

### 📥 Inputs (Parameters)
- **$Model** (Type: EcoATM_PWSMDM.Model)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
3. **Delete**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.