# Microflow Detailed Specification: SUB_Authorization_GetFromForm

### 📥 Inputs (Parameters)
- **$httpRequest** (Type: System.HttpRequest)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_HttpMessage_ParseFormData** (Result: **$Access_Token**)**
2. **Call Microflow **MicrosoftGraph.SUB_HttpMessage_ParseFormData** (Result: **$Id_Token**)**
3. 🔀 **DECISION:** `Check if "Access_Token" exists`
   ➔ **If [true]:**
      1. **Call Microflow **MicrosoftGraph.SUB_HttpMessage_ParseFormData** (Result: **$Token_Type**)**
      2. **Call Microflow **MicrosoftGraph.SUB_HttpMessage_ParseFormData** (Result: **$Expires_In**)**
      3. **Call Microflow **MicrosoftGraph.SUB_HttpMessage_ParseFormData** (Result: **$Scope**)**
      4. **Update **$Authorization** (and Save to DB)
      - Set **Access_Token** = `$Access_Token`
      - Set **Scope** = `$Scope`
      - Set **Expires_In** = `parseInteger($Expires_In)`
      - Set **Token_type** = `$Token_Type`
      - Set **Id_Token** = `$Id_Token`
      - Set **Successful** = `true`
      - Set **Ext_Expires_In** = `parseInteger($Expires_In)`**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.