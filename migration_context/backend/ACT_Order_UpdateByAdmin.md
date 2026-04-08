# Microflow Detailed Specification: ACT_Order_UpdateByAdmin

### 📥 Inputs (Parameters)
- **$Order** (Type: EcoATM_PWS.Order)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
3. **Commit/Save **$Order** to Database**
4. **Close current page/popup**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.