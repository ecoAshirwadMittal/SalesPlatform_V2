# Microflow Detailed Specification: ACT_IncomingEmailConfiguration_FolderSelect

### 📥 Inputs (Parameters)
- **$Folder** (Type: Email_Connector.Folder)
- **$IncomingEmailConfiguration** (Type: Email_Connector.IncomingEmailConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$IncomingEmailConfiguration** (and Save to DB)
      - Set **Folder** = `$Folder/Name`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.