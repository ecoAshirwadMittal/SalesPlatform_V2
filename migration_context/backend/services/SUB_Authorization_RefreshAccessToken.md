# Microflow Detailed Specification: SUB_Authorization_RefreshAccessToken

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `Check if "$Authorization/Refresh_Token" exists`
   ➔ **If [true]:**
      1. **LogMessage**
      2. **Call Microflow **Encryption.Decrypt** (Result: **$DecryptedRefreshToken**)**
      3. **Call Microflow **Encryption.Decrypt** (Result: **$DecryptedSecret**)**
      4. **JavaCallAction**
      5. **Call Microflow **MicrosoftGraph.SUB_Scopes_Get** (Result: **$Scope**)**
      6. **Create Variable **$Location** = `$Authorization/MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/Token_Endpoint`**
      7. **Create Variable **$Request** = `'client_id='+$Authorization/MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/AppId +'&scope='+$Scope +'&refresh_token='+$DecryptedRefreshToken +'&redirect_uri='+$URL+urlEncode('/microsoftgraph/oauth/v2/callback_azure') +'&grant_type=refresh_token &client_secret='+$DecryptedSecret`**
      8. **Call Microflow **MicrosoftGraph.POST** (Result: **$Response**)**
      9. 🔀 **DECISION:** `Successful?`
         ➔ **If [false]:**
            1. **Update **$Authorization** (and Save to DB)
      - Set **Successful** = `false`**
            2. **ImportXml**
            3. **LogMessage**
            4. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. **Call Microflow **MicrosoftGraph.SUB_Authorization_ProcessSuccessfulResponse****
            2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.