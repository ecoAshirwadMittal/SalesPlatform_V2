# Microflow Detailed Specification: ACT_ShowMicroflowSelect

### 📥 Inputs (Parameters)
- **$DeepLink** (Type: DeepLink.DeepLink)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **DeepLink.SUB_ReloadMetaData** (Result: **$Variable**)**
2. **Maps to Page: **DeepLink.Microflow_Select****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.