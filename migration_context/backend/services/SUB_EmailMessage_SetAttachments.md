# Microflow Detailed Specification: SUB_EmailMessage_SetAttachments

### 📥 Inputs (Parameters)
- **$FileDocumentList** (Type: System.FileDocument)
- **$Email** (Type: Email_Connector.EmailMessage)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$FileDocumentList != empty`
   ➔ **If [true]:**
      1. **CreateList**
      2. 🔄 **LOOP:** For each **$File** in **$FileDocumentList**
         │ 1. **Create **Email_Connector.Attachment** (Result: **$Attachment_New**)
      - Set **Attachment_EmailMessage** = `$Email`**
         │ 2. **JavaCallAction**
         │ 3. **Add **$$Attachment_New** to/from list **$AttachmentList****
         └─ **End Loop**
      3. 🏁 **END:** Return `$AttachmentList`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.