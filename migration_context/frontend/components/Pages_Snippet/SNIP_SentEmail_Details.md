# Snippet: SNIP_SentEmail_Details

## Widget Tree

- 📦 **TemplateGrid** [Context] [DP: {Style: Lined}]
  - ⚡ **Button**: Select All [Style: Default]
    ↳ [acti] → **Delete**
  - 🖼️ **Image**: attachment_grey [Class: `attach-icon` | DP: {Spacing left: Outer small}]
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
