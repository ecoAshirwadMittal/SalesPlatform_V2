# Microflow Detailed Specification: ACT_OAuthProvider_Save

### 📥 Inputs (Parameters)
- **$OAuthProvider** (Type: Email_Connector.OAuthProvider)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ScopeSelected_OAuthProvider** via Association from **$OAuthProvider** (Result: **$ScopeSelectedList**)**
2. **Commit/Save **$ScopeSelectedList** to Database**
3. **Commit/Save **$OAuthProvider** to Database**
4. **Show Message (Information): `OAuth configuration created/updated successfully.`**
5. **Close current page/popup**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.