# Nanoflow: ACT_EmailAccount_StartOAuthFlow

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

## 📥 Inputs

- **$EmailAccount** (Email_Connector.EmailAccount)

## ⚙️ Execution Flow

1. **Call Microflow **Email_Connector.SUB_GetOAuthURL** (Result: **$AuthURL**)**
2. **Call JS Action **Email_Connector.JS_OpenSignInPage** (Result: **$ReturnValueName**)**
3. 🏁 **END:** Return `$EmailAccount`

## 🏁 Returns
`Object`
