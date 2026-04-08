# Microflow Detailed Specification: IVK_CreateEmailTemplate_Reset

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MxModelReflection.Module** Filter: `[ModuleName='ForgotPassword']` (Result: **$Module**)**
2. 🔀 **DECISION:** `$Module/SynchronizeObjectsWithinModule = true`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. **DB Retrieve **MxModelReflection.MxObjectType** Filter: `[Module='ForgotPassword'] [Name='ForgotPassword']` (Result: **$MxObject**)**
      3. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObject] [AttributeName='EmailAddress']` (Result: **$MxObjectMember_Email**)**
      4. **Create **MxModelReflection.Token** (Result: **$NewToken_Email**)
      - Set **Prefix** = `'{%'`
      - Set **Suffix** = `'%}'`
      - Set **Token** = `'Email'`
      - Set **Description** = `'This will print the email address from the user account'`
      - Set **Token_MxObjectType_Start** = `$MxObject`
      - Set **Token_MxObjectMember** = `$MxObjectMember_Email`
      - Set **IsOptional** = `true`
      - Set **TokenType** = `MxModelReflection.TokenType.Attribute`**
      5. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObject] [AttributeName='ForgotPasswordURL']` (Result: **$MxObjectMember_ForgotPasswordURL**)**
      6. **Create **MxModelReflection.Token** (Result: **$NewToken_ResetURL**)
      - Set **Prefix** = `'{%'`
      - Set **Suffix** = `'%}'`
      - Set **Token** = `'ResetURL'`
      - Set **Description** = `'This will print the fullname of the user account'`
      - Set **Token_MxObjectType_Start** = `$MxObject`
      - Set **Token_MxObjectMember** = `$MxObjectMember_ForgotPasswordURL`
      - Set **IsOptional** = `true`
      - Set **TokenType** = `MxModelReflection.TokenType.Attribute`**
      7. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObject] [AttributeName='UserFullname']` (Result: **$MxObjectMember_UserFullname**)**
      8. **Create **MxModelReflection.Token** (Result: **$NewToken_FullName**)
      - Set **Prefix** = `'{%'`
      - Set **Suffix** = `'%}'`
      - Set **Token** = `'FullName'`
      - Set **Description** = `'This will print the fullname of the user account'`
      - Set **Token_MxObjectType_Start** = `$MxObject`
      - Set **Token_MxObjectMember** = `$MxObjectMember_UserFullname`
      - Set **IsOptional** = `true`
      - Set **TokenType** = `MxModelReflection.TokenType.Attribute`**
      9. **Create **Email_Connector.EmailTemplate** (Result: **$NewEmailTemplate**)
      - Set **TemplateName** = `'Forgot_Password_Reset'`
      - Set **Subject** = `'Please confirm your password reset request'`
      - Set **UseOnlyPlainText** = `false`
      - Set **EmailTemplate_MxObjectType** = `$MxObject`
      - Set **Content** = `'<p>Dear {%FullName%},<br/> <br/> Please use the link below to confirm your password reset request <a href="{%ResetURL%}">{%ResetURL%}</a><br/> <br/> If you did not request a password reset, you can ignore this message. <br/> </p>'`
      - Set **PlainBody** = `'Dear {%FullName%}, Please use the link below to confirm your password reset request. {%ResetURL%} If you did not request a password reset, you can ignore this message.'`
      - Set **EmailTemplate_Token** = `$NewToken_FullName`
      - Set **EmailTemplate_Token** = `$NewToken_Email`
      - Set **EmailTemplate_Token** = `$NewToken_ResetURL`**
      10. **Maps to Page: **ForgotPassword.EmailTemplate_CreateAndEdit_Reset****
      11. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **MxModelReflection.Module** (Result: **$NewModule**)
      - Set **ModuleName** = `'ForgotPassword'`
      - Set **SynchronizeObjectsWithinModule** = `true`**
      2. **JavaCallAction**
      3. **DB Retrieve **MxModelReflection.MxObjectType** Filter: `[Module='ForgotPassword'] [Name='ForgotPassword']` (Result: **$MxObject**)**
      4. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObject] [AttributeName='EmailAddress']` (Result: **$MxObjectMember_Email**)**
      5. **Create **MxModelReflection.Token** (Result: **$NewToken_Email**)
      - Set **Prefix** = `'{%'`
      - Set **Suffix** = `'%}'`
      - Set **Token** = `'Email'`
      - Set **Description** = `'This will print the email address from the user account'`
      - Set **Token_MxObjectType_Start** = `$MxObject`
      - Set **Token_MxObjectMember** = `$MxObjectMember_Email`
      - Set **IsOptional** = `true`
      - Set **TokenType** = `MxModelReflection.TokenType.Attribute`**
      6. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObject] [AttributeName='ForgotPasswordURL']` (Result: **$MxObjectMember_ForgotPasswordURL**)**
      7. **Create **MxModelReflection.Token** (Result: **$NewToken_ResetURL**)
      - Set **Prefix** = `'{%'`
      - Set **Suffix** = `'%}'`
      - Set **Token** = `'ResetURL'`
      - Set **Description** = `'This will print the fullname of the user account'`
      - Set **Token_MxObjectType_Start** = `$MxObject`
      - Set **Token_MxObjectMember** = `$MxObjectMember_ForgotPasswordURL`
      - Set **IsOptional** = `true`
      - Set **TokenType** = `MxModelReflection.TokenType.Attribute`**
      8. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObject] [AttributeName='UserFullname']` (Result: **$MxObjectMember_UserFullname**)**
      9. **Create **MxModelReflection.Token** (Result: **$NewToken_FullName**)
      - Set **Prefix** = `'{%'`
      - Set **Suffix** = `'%}'`
      - Set **Token** = `'FullName'`
      - Set **Description** = `'This will print the fullname of the user account'`
      - Set **Token_MxObjectType_Start** = `$MxObject`
      - Set **Token_MxObjectMember** = `$MxObjectMember_UserFullname`
      - Set **IsOptional** = `true`
      - Set **TokenType** = `MxModelReflection.TokenType.Attribute`**
      10. **Create **Email_Connector.EmailTemplate** (Result: **$NewEmailTemplate**)
      - Set **TemplateName** = `'Forgot_Password_Reset'`
      - Set **Subject** = `'Please confirm your password reset request'`
      - Set **UseOnlyPlainText** = `false`
      - Set **EmailTemplate_MxObjectType** = `$MxObject`
      - Set **Content** = `'<p>Dear {%FullName%},<br/> <br/> Please use the link below to confirm your password reset request <a href="{%ResetURL%}">{%ResetURL%}</a><br/> <br/> If you did not request a password reset, you can ignore this message. <br/> </p>'`
      - Set **PlainBody** = `'Dear {%FullName%}, Please use the link below to confirm your password reset request. {%ResetURL%} If you did not request a password reset, you can ignore this message.'`
      - Set **EmailTemplate_Token** = `$NewToken_FullName`
      - Set **EmailTemplate_Token** = `$NewToken_Email`
      - Set **EmailTemplate_Token** = `$NewToken_ResetURL`**
      11. **Maps to Page: **ForgotPassword.EmailTemplate_CreateAndEdit_Reset****
      12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.