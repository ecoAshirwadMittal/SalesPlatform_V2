# Microflow Detailed Specification: DL_SingleStringParameterExample

### 📥 Inputs (Parameters)
- **$UrlPath** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **DeepLink.DeeplinkResult** (Result: **$NewExampleEntity**)
      - Set **SessionId** = `$currentSession/SessionId`
      - Set **Value1** = `$UrlPath`
      - Set **Description** = `'This page is showed by microflow ''DL_SingleStringParameterExample''. It copies the value of the input parameter to attribute ''Value1''.'`**
2. **Maps to Page: **DeepLink.ExampleEntity_Details****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.