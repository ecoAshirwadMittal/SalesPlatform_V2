# Page: Folder_Select

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataGrid** [MF: Email_Connector.DS_FolderList] [DP: {Hover style: [object Object]}]
    - ⚡ **Button**: Select [Style: Primary]
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_IncomingEmailConfiguration_FolderSelect`
    - 📊 **Column**: Folder Name [Width: 100]
    ↳ [acti] → **Close Page**
