# Microflow Detailed Specification: SUB_Authentication_GetAdminAccessURL

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetOrCreate** (Result: **$Authorization**)**
2. **JavaCallAction**
3. **Create Variable **$Authority** = `if $Authentication/Authority = MicrosoftGraph.ENUM_Authority.tenant then $Authentication/Tenant_Id else if $Authentication/Authority = MicrosoftGraph.ENUM_Authority.common then toString(MicrosoftGraph.ENUM_Authority.common) else if $Authentication/Authority = MicrosoftGraph.ENUM_Authority.organizations then toString(MicrosoftGraph.ENUM_Authority.organizations) else if $Authentication/Authority = MicrosoftGraph.ENUM_Authority.consumers then toString(MicrosoftGraph.ENUM_Authority.consumers) else $Authentication/Tenant_Id`**
4. **Create Variable **$Location** = `@MicrosoftGraph.LoginLocation+'/'+$Authority+'/adminconsent'+'?' +'client_id='+$Authentication/AppId +'&redirect_uri='+$URL+urlEncode('/microsoftgraph/oauth/v2/callback_azure') +'&state='+$Authorization/State`**
5. **Commit/Save **$Authentication** to Database**
6. **Commit/Save **$Authorization** to Database**
7. 🏁 **END:** Return `$Location`

**Final Result:** This process concludes by returning a [String] value.