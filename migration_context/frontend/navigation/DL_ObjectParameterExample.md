# Microflow Detailed Specification: DL_ObjectParameterExample

### 📥 Inputs (Parameters)
- **$Language** (Type: System.Language)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **DeepLink.DeeplinkResult** (Result: **$NewDeeplinkResult**)
      - Set **Description** = `'This page is showed by microflow ''DL_ObjectParameterExample''. This microflow has an object input parameter of entity ''Language''. The object input parameter is: ' + (if $Language !=empty then 'System.Language' else 'Null') + ' Attribute values: - Code: '+$Language/Code + ' - Description: '+$Language/Description`
      - Set **SessionId** = `$currentSession/SessionId`**
2. **Maps to Page: **DeepLink.ExampleEntity_Details****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.