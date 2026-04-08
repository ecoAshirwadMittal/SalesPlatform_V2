# Microflow Detailed Specification: ACT_AuditRestAPICalls

### 📥 Inputs (Parameters)
- **$HttpResponse** (Type: System.HttpResponse)
- **$Request** (Type: Variable)
- **$Response** (Type: Variable)
- **$ENUM_Method** (Type: Variable)
- **$Success** (Type: Variable)
- **$URL** (Type: Variable)
- **$Error** (Type: System.Error)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [true]:**
      1. **Create **EcoATM_PWSIntegration.Integration** (Result: **$NewIntegration**)
      - Set **EndTime** = `[%CurrentDateTime%]`
      - Set **ResponseCode** = `if($latestHttpResponse/StatusCode!=empty) then toString($latestHttpResponse/StatusCode) else 'NA'`
      - Set **IsSuccessful** = `$Success`
      - Set **URL** = `$URL`
      - Set **Method** = `$ENUM_Method`
      - Set **Request** = `$Request`
      - Set **Response** = `$Response`
      - Set **StackTrace** = `$Error/Stacktrace`
      - Set **ErrorMessage** = `$Error/Message`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.