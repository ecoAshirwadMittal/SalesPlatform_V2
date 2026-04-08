# Page: ConnectionDetails_NewEdit_Step2

**Allowed Roles:** SnowflakeRESTSQL.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [Click] → **Page**: `SnowflakeRESTSQL.ConnectionDetails_NewEdit_Step1`
      ↳ [acti] → **Page**: `SnowflakeRESTSQL.ConnectionDetails_NewEdit_Step1`
      ↳ [acti] → **Page**: `SnowflakeRESTSQL.ConnectionDetails_NewEdit_Step1`
    ↳ [acti] → **Nanoflow**: `SnowflakeRESTSQL.ACT_DoNothing`
    ↳ [acti] → **Nanoflow**: `SnowflakeRESTSQL.ACT_DoNothing`
  - 📂 **GroupBox**: "TEST 1: Generate JWT" [DP: {Spacing bottom: Outer large}]
      ↳ [acti] → **Microflow**: `SnowflakeRESTSQL.ACT_ConnectionDetails_GenerateJWT`
    - 📦 **DataView** [MF: SnowflakeRESTSQL.DS_JWT]
      - 📝 **DatePicker**: datePicker1
  - 📂 **GroupBox**: "TEST 2: Validate connection to Snowflake"
      ↳ [acti] → **Microflow**: `SnowflakeRESTSQL.ACT_ConnectionDetails_ValidateConnectionWithJWT`
    ↳ [acti] → **Close Page**
    ↳ [acti] → **Page**: `SnowflakeRESTSQL.ConnectionDetails_NewEdit_Step1`
