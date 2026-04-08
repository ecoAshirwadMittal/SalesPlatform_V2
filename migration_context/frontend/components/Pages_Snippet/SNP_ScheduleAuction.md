# Snippet: SNP_ScheduleAuction

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Round 1" [Class: `rounds-title` | Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer large}]
  - 🔤 **Text**: "Buyers" [Class: `rounds-card-buyers-label`]
  - 🔤 **Text**: "Total" [Class: `rounds-card-buyers-label`]
  - 🔤 **Text**: "DW Only" [Class: `rounds-card-buyers-label`]
  - 📝 **DatePicker**: datePicker1
  - 📝 **DatePicker**: datePicker3 [Class: `scheduletime`]
  - 📝 **DatePicker**: datePicker2 👁️ (If: `not($currentObject/Round1_Start_DateTime > $currentObject/Round1_End_DateTime)`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **DatePicker**: datePicker13 [Class: `my-date-picker`] 👁️ (If: `$currentObject/Round1_Start_DateTime > $currentObject/Round1_End_DateTime`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **DatePicker**: datePicker4 [Class: `scheduletime`] 👁️ (If: `not($currentObject/Round1_Start_DateTime > $currentObject/Round1_End_DateTime)`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **DatePicker**: datePicker14 [Class: `my-date-picker`] 👁️ (If: `$currentObject/Round1_Start_DateTime > $currentObject/Round1_End_DateTime`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **CheckBox**: checkBox1
  - 🔤 **Text**: "Round 2" [Class: `rounds-title` | Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer large}]
  - 🧩 **Tooltip** (ID: `com.mendix.widget.web.tooltip.Tooltip`)
      ➤ **trigger** (Widgets)
        - 🖼️ **Image**: message_square_warning
      - renderMethod: custom
      ➤ **htmlMessage** (Widgets)
      - tooltipPosition: top
      - arrowPosition: end
      - openOn: hover
  - 📝 **DatePicker**: datePicker5 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker6 🔒 [Read-Only] [Class: `scheduletime`]
  - 📝 **DatePicker**: datePicker7 👁️ (If: `not($currentObject/Round2_Start_DateTime > $currentObject/Round2_End_DateTime)`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **DatePicker**: datePicker15 [Class: `my-date-picker`] 👁️ (If: `$currentObject/Round2_Start_DateTime > $currentObject/Round2_End_DateTime`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **DatePicker**: datePicker8 [Class: `scheduletime`] 👁️ (If: `not($currentObject/Round2_Start_DateTime > $currentObject/Round2_End_DateTime)`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **DatePicker**: datePicker16 [Class: `my-date-picker`] 👁️ (If: `$currentObject/Round2_Start_DateTime > $currentObject/Round2_End_DateTime`)
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_SetRoundStartDefaults`
  - 📝 **CheckBox**: checkBox2
  - 🔤 **Text**: "Upsell Round" [Class: `rounds-title` | Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer large}]
  - 📝 **DatePicker**: datePicker9 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker10 🔒 [Read-Only] [Class: `scheduletime`]
  - 📝 **DatePicker**: datePicker11 👁️ (If: `not($currentObject/Round3_Start_DateTime > $currentObject/Round3_End_Datetime)`)
  - 📝 **DatePicker**: datePicker17 [Class: `my-date-picker`] 👁️ (If: `$currentObject/Round3_Start_DateTime > $currentObject/Round3_End_Datetime`)
  - 📝 **DatePicker**: datePicker12 [Class: `scheduletime`] 👁️ (If: `not($currentObject/Round3_Start_DateTime > $currentObject/Round3_End_Datetime)`)
  - 📝 **DatePicker**: datePicker18 [Class: `my-date-picker`] 👁️ (If: `$currentObject/Round3_Start_DateTime > $currentObject/Round3_End_Datetime`)
