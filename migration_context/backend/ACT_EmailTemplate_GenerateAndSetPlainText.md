# Microflow Detailed Specification: ACT_EmailTemplate_GenerateAndSetPlainText

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Update **$EmailTemplate**
      - Set **PlainBody** = `$PlainText`**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.