# Microflow Detailed Specification: ACT_EmailMessage_SendEmailAndClosePage

### 📥 Inputs (Parameters)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Email_Connector.SUB_SendEmail** (Result: **$Variable**)**
2. 🔀 **DECISION:** `$Variable`
   ➔ **If [true]:**
      1. **Retrieve related **EmailMessage_EmailAccount** via Association from **$EmailMessage** (Result: **$EmailAccount**)**
      2. **Update **$EmailAccount**
      - Set **ComposeEmail** = `false`**
      3. **Close current page/popup**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.