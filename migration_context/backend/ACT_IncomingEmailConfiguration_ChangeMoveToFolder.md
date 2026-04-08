# Microflow Detailed Specification: ACT_IncomingEmailConfiguration_ChangeMoveToFolder

### 📥 Inputs (Parameters)
- **$Folder** (Type: Email_Connector.Folder)
- **$IncomingEmailConfiguration** (Type: Email_Connector.IncomingEmailConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$IncomingEmailConfiguration**
      - Set **MoveFolder** = `$Folder/Name`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.