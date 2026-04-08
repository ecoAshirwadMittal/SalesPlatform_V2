# Microflow Detailed Specification: ACT_Order_DeleteByAdmin

### 📥 Inputs (Parameters)
- **$Order** (Type: EcoATM_PWS.Order)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
3. **Delete**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.