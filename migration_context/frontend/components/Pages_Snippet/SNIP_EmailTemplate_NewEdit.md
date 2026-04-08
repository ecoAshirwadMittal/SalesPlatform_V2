# Snippet: SNIP_EmailTemplate_NewEdit

## Widget Tree

  ↳ [acti] → **OpenLink**
  ↳ [acti] → **Microflow**: `Email_Connector.OCH_EmailTemplate_ToggleRecipients`
  ↳ [acti] → **OpenLink**
- ⚡ **Button**: radioButtons1 [DP: {Align self: Left}]
- ⚡ **Button**: radioButtons2 [DP: {Align self: Left}]
- 📑 **TabContainer**
  - 📑 **Tab**: "Plain text"
    - 📝 **CheckBox**: checkBox1
    - ⚡ **Button**: Copy from html text [Style: Default] 👁️ (If: `not($EmailTemplate/UseOnlyPlainText) and ($EmailTemplate/Content != empty and $EmailTemplate/Content != '')`)
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailTemplate_GenerateAndSetPlainText`
  - 📑 **Tab**: "HTML Preview" 👁️ (If UseOnlyPlainText is true/false)
    - 🧩 **Rich Text** (ID: `com.mendix.widget.custom.richtext.RichText`)
        - stringAttribute: [Attr: Email_Connector.EmailTemplate.Content]
        - readOnlyStyle: text
        - editorType: classic
        - preset: full
        - widthUnit: percentage
        - width: 100
        - heightUnit: percentageOfWidth
        - height: 75
        - toolbarConfig: basic
        - enterMode: paragraph
        - shiftEnterMode: paragraph
        - advancedContentFilter: auto
  - 📑 **Tab**: "Attachments"
    - 📦 **DataGrid** [Context]
      - ⚡ **Button**: New [Style: Default]
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_Attachment_Download`
      - ⚡ **Button**: Delete [Style: Default]
        ↳ [acti] → **Delete**
      - 📊 **Column**: Name [Width: 100]
  ↳ [acti] → **OpenLink**
- 📦 **DataView** [MF: Email_Connector.DS_ModelReflectionChecker]
    ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailTemplate_ShowModelReflectionPage`
  - 📦 **DataView** [Context] [DP: {Spacing top: Outer large}] 👁️ (If ModelReflectionSynced is true/false)
    - 📝 **ReferenceSelector**: referenceSelector1
    - 📦 **DataGrid** [Association: undefined]
      - ⚡ **Button**: New [Style: Default]
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_Emailtemplate_CreateToken`
      - ⚡ **Button**: Edit [Style: Default]
        ↳ [acti] → **Page**: `MxModelReflection.Token_NewEdit`
      - ⚡ **Button**: Delete [Style: Default]
        ↳ [acti] → **Delete**
      - 📊 **Column**: Placeholder Name [Width: 33]
      - 📊 **Column**: Value [Width: 67]
