# Snippet: SNIP_ComposeEmail

## Widget Tree

  ↳ [acti] → **Microflow**: `Email_Connector.OCH_EmailMessage_ToggleRecipients`
- ⚡ **Button**: radioButtons3 [DP: {Align self: Left}]
  ↳ [Change] → **Microflow**: `Email_Connector.VAL_Pk12Certificate`
- ⚡ **Button**: radioButtons4 [DP: {Align self: Left}]
  ↳ [Change] → **Microflow**: `Email_Connector.VAL_LDAPConfiguration`
- 🧩 **Rich Text** [DP: {Spacing top: Outer small}] (ID: `com.mendix.widget.custom.richtext.RichText`)
    - stringAttribute: [Attr: Email_Connector.EmailMessage.Content]
    - readOnlyStyle: text
    - editorType: classic
    - preset: standard
    - widthUnit: percentage
    - width: 100
    - heightUnit: percentageOfWidth
    - height: 75
    - toolbarConfig: basic
    - enterMode: paragraph
    - shiftEnterMode: paragraph
    - advancedContentFilter: auto
- 📦 **DataGrid** [Association: undefined]
  - 📊 **Column**: Name [Width: 50]
  - 📊 **Column**: Size [Width: 50]
