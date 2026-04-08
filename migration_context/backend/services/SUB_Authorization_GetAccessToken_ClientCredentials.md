# Microflow Detailed Specification: SUB_Authorization_GetAccessToken_ClientCredentials

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.Decrypt** (Result: **$Secret**)**
2. **JavaCallAction**
3. **Create Variable **$Location** = `$Authorization/MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/Token_Endpoint`**
4. **Create Variable **$Request** = `'client_id='+$Authorization/MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/AppId +'&scope=https%3A%2F%2Fgraph.microsoft.com/.default' +'&grant_type=client_credentials &client_secret='+$Secret`**
5. **Call Microflow **MicrosoftGraph.POST** (Result: **$Response**)**
6. 🔀 **DECISION:** `Successful?`
   ➔ **If [true]:**
      1. **Update **$Authorization**
      - Set **AdminConsent** = `true`
      - Set **GrantFlow** = `MicrosoftGraph.ENUM_GrantFlow.client_credentials`**
      2. **Call Microflow **MicrosoftGraph.SUB_Authorization_ProcessSuccessfulResponse****
      3. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Update **$Authorization**
      - Set **Successful** = `false`**
      2. **ImportXml**
      3. **LogMessage**
      4. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.