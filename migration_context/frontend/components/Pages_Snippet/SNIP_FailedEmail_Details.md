# Snippet: SNIP_FailedEmail_Details

## Widget Tree

- 📦 **TemplateGrid** [Context] [DP: {Style: Lined}]
  - ⚡ **Button**: Search [Style: Default] [Style: `border: 1px solid #CED0D3;
color: #6C717C;`]
  - ⚡ **Button**: Select All [Style: Default]
    ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailMessageList_ResetQueuedStatus`
    ↳ [acti] → **Delete**
  - 🖼️ **Image**: attachment_grey [Class: `attach-icon` | DP: {Spacing left: Outer small}]
  - 🧩 **Badge** [DP: {Align self: Right, Spacing top: Outer small}] (ID: `com.mendix.widget.custom.badge.Badge`)
      - type: badge
      - value: Failed after attempts: {1}
- 📦 **DataView** [Context] [DP: {Spacing left: Outer medium, Spacing top: Outer small}]
  - 📑 **TabContainer**
    - 📑 **Tab**: "Details"
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
    - 📑 **Tab**: "Error message" 👁️ (If Status is QUEUED/SENT/FAILED/ERROR/RECEIVED/(empty))
