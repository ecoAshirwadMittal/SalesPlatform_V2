# Microflow Detailed Specification: DS_GetParametersFromMicroflow

### 📥 Inputs (Parameters)
- **$Microflow** (Type: Variable)
- **$Type** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **List Operation: **Filter** on **$undefined** where `$Type` (Result: **$StringParameterList**)**
3. 🏁 **END:** Return `$StringParameterList`

**Final Result:** This process concludes by returning a [List] value.