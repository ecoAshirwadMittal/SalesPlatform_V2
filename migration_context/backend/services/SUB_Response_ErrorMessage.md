# Microflow Detailed Specification: SUB_Response_ErrorMessage

### 📥 Inputs (Parameters)
- **$HttpResponse** (Type: System.HttpResponse)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **ImportXml**
2. **Retrieve related **Error_Response** via Association from **$ErrorResponse** (Result: **$Error**)**
3. **Update **$Error**
      - Set **Error_Authorization** = `$Authorization`**
4. 🏁 **END:** Return `$Error/Message`

**Final Result:** This process concludes by returning a [String] value.