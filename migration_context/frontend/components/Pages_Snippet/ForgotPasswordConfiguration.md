# Snippet: ForgotPasswordConfiguration

## Widget Tree

- 📦 **DataView** [MF: ForgotPassword.DV_GetConfiguration]
  - 📑 **TabContainer**
    - 📑 **Tab**: "Reset Password Email"
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_LaunchEmailConnectorOverview`
      - 📦 **DataGrid** [Context]
        - ⚡ **Button**: Search [Style: Default]
          ↳ [acti] → **Microflow**: `ForgotPassword.IVK_CreateEmailTemplate_Reset`
          ↳ [acti] → **Page**: `ForgotPassword.EmailTemplate_CreateAndEdit_Reset`
          ↳ [acti] → **Delete**
        - 📊 **Column**: Template name [Width: 50]
        - 📊 **Column**: Description [Width: 50]
    - 📑 **Tab**: "Signup Email"
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_LaunchEmailConnectorOverview`
      - ⚡ **Button**: Create email template [Style: Primary] [Style: `margin-right: 10px;`] 👁️ (If HasSignUpTemplate is true/false)
        ↳ [acti] → **Microflow**: `ForgotPassword.IVK_CreateEmailTemplate_Signup`
      - ⚡ **Button**: Reset email template [Style: Primary] [Style: `margin-right: 10px;`] 👁️ (If HasSignUpTemplate is true/false)
        ↳ [acti] → **Microflow**: `ForgotPassword.IVK_CreateEmailTemplate_Signup`
      - 📦 **DataView** [Context]
        - 📦 **DataView** [MF: ForgotPassword.DS_CreateSMTPObject]
          - 📝 **ReferenceSelector**: referenceSelector3
          ↳ [acti] → **Save Changes**
    - 📑 **Tab**: "Deeplink"
      - ⚡ **Button**: Create deeplink [Style: Primary]
        ↳ [acti] → **Microflow**: `ForgotPassword.IVK_CreateDeeplink`
      - ⚡ **Button**: Reset Deeplink Configuration [Style: Primary]
        ↳ [acti] → **Microflow**: `ForgotPassword.IVK_CreateDeeplink`
      - 📦 **DataView** [Context]
          ↳ [Change] → **Microflow**: `ForgotPassword.OCh_Name`
          ↳ [acti] → **OpenLink**
        - 📂 **GroupBox**: "groupBox" [Class: `groupbox-callout-info` | Style: `margin-bottom: 7px;`]
          - 🔤 **Text**: "The name of the link, as seen by the user. If name is set to 'product' the generated deeplink will be (for example):" [Style: `display: inline;`]
          - 🔤 **Text**: "http://yourhost/link/product/17" [Style: `display: inline;
margin-left: 3px;`]
        - 📂 **GroupBox**: "groupBox2" [Class: `groupbox-callout-info` | Style: `margin-bottom: 7px;`]
          - 🔤 **Text**: "The fully qualified name of the microflow that will be invoked by this deeplink. For example:" [Style: `display: inline;`]
          - 🔤 **Text**: "MyFirstModule.ShowProduct" [Style: `display: inline;
margin-left: 3px;`]
        - 📝 **CheckBox**: checkBox5 🔒 [Read-Only]
        - 📂 **GroupBox**: "groupBox2223" [Class: `groupbox-callout-info` | Style: `margin-bottom: 7px;`]
          - 🔤 **Text**: "Example: with a URL like "http://appname/link/deeplinkname/stringtext?param=value&other=test", The microflow that is called can receive two String parameters named "param" and "other","
        - 📂 **GroupBox**: "groupBox22223" [Class: `groupbox-callout-info` | Style: `margin-bottom: 7px;`]
          - 🔤 **Text**: "The object type to be passed to the microflow. If empty, no arguments will be passed to the microflow."
        - 📝 **CheckBox**: checkBox1 🔒 [Read-Only]
        - 📝 **ReferenceSelector**: referenceSelector1
        - 📂 **GroupBox**: "groupBox2222223" [Class: `groupbox-callout-info` | Style: `margin-bottom: 7px;`]
          - 🔤 **Text**: "Language to use for the deeplink. Useful for guest sessions"
        - 📝 **CheckBox**: checkBox3 🔒 [Read-Only]
        - 📂 **GroupBox**: "groupBox2222222" [Class: `groupbox-callout-info` | Style: `margin-bottom: 7px;`]
          - 🔤 **Text**: "When the application is initially visited using this deeplink, this deeplink will be used to determine the landing page for the current session. (E.g. clicking Home or refreshing/reloading the application)"
        - 📂 **GroupBox**: "groupBox22222222" [Class: `groupbox-callout-info`]
          - 🔤 **Text**: "A user will be redirected to a custom index file (located in the theme folder) specified here."
          ↳ [acti] → **Save Changes**
    - 📑 **Tab**: "Open Requests"
      - 📦 **DataGrid** [Context]
        - ⚡ **Button**: Edit [Style: Default]
          ↳ [acti] → **Page**: `ForgotPassword.ForgotPassword_Edit`
        - ⚡ **Button**: Delete [Style: Danger]
          ↳ [acti] → **Delete**
        - ⚡ **Button**: Select all [Style: Default]
        - ⚡ **Button**: Deselect all [Style: Default]
        - 📊 **Column**: Email address [Width: 17]
        - 📊 **Column**: Username [Width: 14]
        - 📊 **Column**: User fullname [Width: 12]
        - 📊 **Column**: Forgot password GUID [Width: 18]
        - 📊 **Column**: Forgot password URL [Width: 15]
        - 📊 **Column**: Created on [Width: 12]
        - 📊 **Column**: Valid untill [Width: 12]
