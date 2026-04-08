# Microflow Detailed Specification: ACT_GetAuthorization

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create Variable **$AuthDisplayName** = `@EcoATM_Direct_Sharepoint.SharepointAuthenticationDisplayName`**
3. **DB Retrieve **MicrosoftGraph.Authorization** Filter: `[MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/DisplayName = $AuthDisplayName]` (Result: **$Authorization**)**
4. **LogMessage**
5. 🏁 **END:** Return `$Authorization`

**Final Result:** This process concludes by returning a [Object] value.