# Microflow Detailed Specification: ACT_EmailTemplate_DuplicateAndShowPage

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
2. **Call Microflow **Email_Connector.SUB_DuplicateTokenList** (Result: **$NewTokenList**)**
3. **Retrieve related **Attachment_EmailTemplate** via Association from **$EmailTemplate** (Result: **$AttachmentList**)**
4. **Create **Email_Connector.EmailTemplate** (Result: **$NewEmailTemplate**)
      - Set **To** = `$EmailTemplate/To`
      - Set **CC** = `$EmailTemplate/CC`
      - Set **BCC** = `$EmailTemplate/BCC`
      - Set **Subject** = `$EmailTemplate/Subject`
      - Set **Content** = `$EmailTemplate/Content`
      - Set **PlainBody** = `$EmailTemplate/PlainBody`
      - Set **CreationDate** = `[%CurrentDateTime%]`
      - Set **FromDisplayName** = `$EmailTemplate/FromDisplayName`
      - Set **UseOnlyPlainText** = `$EmailTemplate/UseOnlyPlainText`
      - Set **ReplyTo** = `$EmailTemplate/ReplyTo`
      - Set **FromAddress** = `$EmailTemplate/FromAddress`
      - Set **Signed** = `$EmailTemplate/Signed`
      - Set **Encrypted** = `$EmailTemplate/Encrypted`
      - Set **EmailTemplate_MxObjectType** = `$EmailTemplate/Email_Connector.EmailTemplate_MxObjectType`
      - Set **EmailTemplate_Token** = `$NewTokenList`**
5. **Call Microflow **Email_Connector.SUB_GetEmailTemplateAttachments** (Result: **$AttachmentList_New**)**
6. **Update **$NewEmailTemplate**
      - Set **TemplateName** = `$EmailTemplate/TemplateName`
      - Set **hasAttachment** = `$AttachmentList_New!=empty`**
7. **Maps to Page: **Email_Connector.EmailTemplate_NewEdit****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.