# Microflow Detailed Specification: SUB_Authorization_GetAccessToken_AuthorizationCode

### 📥 Inputs (Parameters)
- **$Code** (Type: Variable)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `Check if "Code" exists`
   ➔ **If [false]:**
      1. **LogMessage**
      2. **Update **$Authorization**
      - Set **Successful** = `false`**
      3. **LogMessage**
      4. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **Call Microflow **Encryption.Decrypt** (Result: **$Secret**)**
      2. **JavaCallAction**
      3. **Create Variable **$Location** = `$Authorization/MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/Token_Endpoint`**
      4. **Call Microflow **MicrosoftGraph.SUB_Scopes_Get** (Result: **$Scope**)**
      5. **Create Variable **$Request** = `'client_id='+$Authorization/MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/AppId +'&scope='+$Scope +'&code='+$Code +'&redirect_uri='+$URL+urlEncode('/microsoftgraph/oauth/v2/callback_azure') +'&grant_type=authorization_code &client_secret='+$Secret`**
      6. **Call Microflow **MicrosoftGraph.POST** (Result: **$Response**)**
      7. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **Update **$Authorization**
      - Set **AdminConsent** = `false`
      - Set **GrantFlow** = `MicrosoftGraph.ENUM_GrantFlow.authorization_code`**
            2. **Call Microflow **MicrosoftGraph.SUB_Authorization_ProcessSuccessfulResponse****
            3. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Update **$Authorization**
      - Set **Successful** = `false`**
            2. **ImportXml**
            3. **LogMessage**
            4. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.