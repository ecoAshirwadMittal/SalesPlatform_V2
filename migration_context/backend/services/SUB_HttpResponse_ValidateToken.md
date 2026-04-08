# Microflow Detailed Specification: SUB_HttpResponse_ValidateToken

### 📥 Inputs (Parameters)
- **$httpResponse** (Type: System.HttpResponse)
- **$validationToken** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `Check if "$validationToken" exists`
   ➔ **If [true]:**
      1. **LogMessage**
      2. **Update **$httpResponse**
      - Set **Content** = `urlDecode($validationToken)`**
      3. **Create **System.HttpHeader** (Result: **$NewHttpHeader**)
      - Set **HttpHeaders** = `$httpResponse`
      - Set **Key** = `'Content-Type'`
      - Set **Value** = `'text/plain'`**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.