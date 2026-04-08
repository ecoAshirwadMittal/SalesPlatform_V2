# Page: ConnectionDetails_NewEdit_Step1

**Allowed Roles:** SnowflakeRESTSQL.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **Nanoflow**: `SnowflakeRESTSQL.ACT_DoNothing`
    ↳ [acti] → **Nanoflow**: `SnowflakeRESTSQL.ACT_DoNothing`
    ↳ [Click] → **Microflow**: `SnowflakeRESTSQL.ACT_ConnectionDetails_SaveAndNext`
      ↳ [acti] → **Microflow**: `SnowflakeRESTSQL.ACT_ConnectionDetails_SaveAndNext`
      ↳ [acti] → **Microflow**: `SnowflakeRESTSQL.ACT_ConnectionDetails_SaveAndNext`
  - ⚡ **Button**: radioButtons1
    ↳ [acti] → **Nanoflow**: `SnowflakeRESTSQL.ACT_DoNothing`
    ↳ [acti] → **OpenLink**
  - 📂 **GroupBox**: "Private key"
      ↳ [acti] → **OpenLink**
    - 📦 **DataView** [Context]
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `SnowflakeRESTSQL.ACT_ConnectionDetails_Save`
    ↳ [acti] → **Microflow**: `SnowflakeRESTSQL.ACT_ConnectionDetails_SaveAndNext`
