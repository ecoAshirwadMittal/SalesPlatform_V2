# Microflow Detailed Specification: DS_MicroflowByDeeplink

### 📥 Inputs (Parameters)
- **$DeepLink** (Type: DeepLink.DeepLink)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **DeepLink.Microflow** (Result: **$NewMicroflow**)**
2. **Call Microflow **DeepLink.UpdateMicroflowMetaData****
3. 🏁 **END:** Return `$NewMicroflow`

**Final Result:** This process concludes by returning a [Object] value.