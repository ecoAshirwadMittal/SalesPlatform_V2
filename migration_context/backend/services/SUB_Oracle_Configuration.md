# Microflow Detailed Specification: SUB_Oracle_Configuration

### 📥 Inputs (Parameters)
- **$DataContent** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSIntegration.PWSConfiguration**  (Result: **$PWSConfigurationList**)**
2. **ImportXml**
3. **Delete**
4. **Commit/Save **$NewPWSConfiguration** to Database**
5. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.