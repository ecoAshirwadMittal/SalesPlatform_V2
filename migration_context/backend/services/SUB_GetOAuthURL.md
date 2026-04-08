# Microflow Detailed Specification: SUB_GetOAuthURL

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EmailAccount_OAuthProvider** via Association from **$EmailAccount** (Result: **$OAuthProvider**)**
2. **Retrieve related **ScopeSelected_OAuthProvider** via Association from **$OAuthProvider** (Result: **$ScopeSelectedList**)**
3. **Create Variable **$ScopeQueryParamValue** = `''`**
4. 🔄 **LOOP:** For each **$IteratorScopeSelected** in **$ScopeSelectedList**
   │ 1. **Update Variable **$ScopeQueryParamValue** = `if $ScopeQueryParamValue = '' then $IteratorScopeSelected/ScopeString else $ScopeQueryParamValue + ' ' + $IteratorScopeSelected/ScopeString`**
   └─ **End Loop**
5. **Create Variable **$ScopeQueryParam** = `'&scope='+urlEncode($ScopeQueryParamValue)`**
6. **Call Microflow **Email_Connector.SUB_GenerateOAuthNonce** (Result: **$nonce**)**
7. **Create **Email_Connector.OAuthNonce** (Result: **$NewOAuthNonce**)
      - Set **State** = `$nonce`
      - Set **OAuthNonce_EmailAccount** = `$EmailAccount`**
8. **Create Variable **$AuthURL** = `$OAuthProvider/AuthorizationEndpoint + '?client_id='+$OAuthProvider/ClientID + '&response_type=code' + '&response_mode=query' + $ScopeQueryParam + '&login_hint=' + $EmailAccount/Username + '&redirect_uri=' + $OAuthProvider/CallbackURL + '&state=' + $nonce`**
9. 🏁 **END:** Return `$AuthURL`

**Final Result:** This process concludes by returning a [String] value.