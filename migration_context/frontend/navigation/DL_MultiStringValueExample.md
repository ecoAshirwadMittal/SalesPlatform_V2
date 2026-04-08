# Microflow Detailed Specification: DL_MultiStringValueExample

### 📥 Inputs (Parameters)
- **$Parameter_1** (Type: Variable)
- **$Parameter_2** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **DeepLink.DeeplinkResult** (Result: **$NewExampleEntity**)
      - Set **Value1** = `$Parameter_1`
      - Set **Value2** = `$Parameter_2`
      - Set **SessionId** = `$currentSession/SessionId`
      - Set **Description** = `'This page is showed by microflow ''DL_MultiStringValueExample''. It copies the values of the input parameters to attributes ''Value1'' and ''Value2''.'`**
2. **Maps to Page: **DeepLink.ExampleEntity_Details****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.