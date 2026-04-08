# Snippet: SNIP_EmailConnector_Overview

## Widget Tree

  ↳ [acti] → **Microflow**: `Email_Connector.ACT_ShowAccountSettingsPage`
  ↳ [acti] → **Microflow**: `Email_Connector.ACT_ShowAccountSettingsPage`
  ↳ [acti] → **Page**: `Email_Connector.EmailAccount_Manage_View`
  ↳ [acti] → **Page**: `Email_Connector.OAuthProvider_Overview`
  ↳ [acti] → **Page**: `Email_Connector.EmailTemplate_Overview`
- 📑 **TabContainer** [DP: {Style: Lined}]
  - 📑 **Tab**: "Inbox"
      ↳ [acti] → **Nanoflow**: `Email_Connector.ACT_Toggle_EmailCompose`
    - ⚡ **Button**: Fetch Emails [Style: Default] [DP: {Full width: [object Object]}]
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_RetrieveEmails`
    - 📦 **TemplateGrid** [Context] [Style: `border-top: 1px solid #CED0D3;` | DP: {Style: Lined, Spacing top: Outer medium}]
      - ⚡ **Button**: Search [Style: Default] [Style: `border: 1px solid #CED0D3;
color: #6C717C;`]
      - ⚡ **Button**: Select All [Style: Default]
        ↳ [acti] → **Delete**
      - 🖼️ **Image**: attachment_grey [Class: `attach-icon` | DP: {Spacing left: Outer small}]
    - 📦 **DataView** [Context] [DP: {Spacing left: Outer medium, Spacing top: Outer small}] 👁️ (If ComposeEmail is true/false)
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailMessage_ComposeReply`
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailMessage_Delete`
      - 🔤 **Text**: "From:" [DP: {Align self: Left}]
      - 🔤 **Text**: "To:"
      - 🔤 **Text**: "Reply To:" [Style: `white-space: nowrap`]
      - 🔤 **Text**: "Cc:"
      - 🔤 **Text**: "Bcc:"
      - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
          - tagName: div
          - tagNameCustom: div
          - tagContentMode: innerHTML
          - tagContentHTML: {1}
      - 📦 **DataGrid** [Context] 👁️ (If hasAttachments is true/false)
          ↳ [acti] → **Microflow**: `Email_Connector.ACT_Attachment_Download`
        - 📊 **Column**: Name [Width: 25]
        - 📊 **Column**: Size [Width: 25]
        - 📊 **Column**: Position [Width: 25]
        - 📊 **Column**: Content ID [Width: 25]
    - 📦 **DataView** [MF: Email_Connector.ACT_EmailMessage_ComposeNewEmail]
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailMessage_SendComposedEmail`
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_Attachment_CreateNew`
        ↳ [acti] → **Nanoflow**: `Email_Connector.ACT_EmailMessage_Discard`
  - 📑 **Tab**: "Sent Items"
  - 📑 **Tab**: "Queued"
  - 📑 **Tab**: "Failed"
