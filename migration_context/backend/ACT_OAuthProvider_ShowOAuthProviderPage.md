# Microflow Detailed Specification: ACT_OAuthProvider_ShowOAuthProviderPage

### 📥 Inputs (Parameters)
- **$OAuthProvider** (Type: Email_Connector.OAuthProvider)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EmailAccount_OAuthProvider** via Association from **$OAuthProvider** (Result: **$EmailAccountList**)**
2. **List Operation: **Head** on **$undefined** (Result: **$EmailAccount**)**
3. **Maps to Page: **Email_Connector.OAuthProvider_NewEdit****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.