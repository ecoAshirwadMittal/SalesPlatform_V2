# Microflow Detailed Specification: ACT_EmailTemplate_ShowModelReflectionPage

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Commit/Save **$EmailTemplate** to Database**
2. **Close current page/popup**
3. **Maps to Page: **MxModelReflection.MxObjects_Overview****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.