# Snippet: SNIP_EmailTemplate_Overview

## Widget Tree

  ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_LaunchEmailConnectorOverview`
  ↳ [acti] → **OpenLink**
- 📦 **DataGrid** [Context]
  - ⚡ **Button**: Search [Style: Default] [Style: `border: 1px solid #CED0D3;
color: #6C717C;`]
  - ⚡ **Button**: New [Style: Primary]
  - ⚡ **Button**: Edit [Style: Default]
    ↳ [acti] → **Page**: `Email_Connector.EmailTemplate_NewEdit`
  - ⚡ **Button**: Duplicate [Style: Default]
    ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailTemplate_DuplicateAndShowPage`
  - ⚡ **Button**: Delete [Style: Default]
    ↳ [acti] → **Delete**
  - 📊 **Column**: Template Name [Width: 18]
  - 📊 **Column**: From Name [Width: 17]
  - 📊 **Column**: Subject [Width: 43]
  - 📊 **Column**: Attachment [Width: 10]
  - 📊 **Column**: Created on [Width: 12]
