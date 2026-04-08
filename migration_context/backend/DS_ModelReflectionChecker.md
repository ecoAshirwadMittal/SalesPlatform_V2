# Microflow Detailed Specification: DS_ModelReflectionChecker

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MxModelReflection.MxObjectType**  (Result: **$NewMxObjectType**)**
2. **Create **Email_Connector.ModelReflectionChecker** (Result: **$NewModelReflectionChecker**)
      - Set **ModelReflectionChecker_EmailTemplate** = `$EmailTemplate`
      - Set **ModelReflectionSynced** = `$NewMxObjectType != empty`**
3. 🏁 **END:** Return `$NewModelReflectionChecker`

**Final Result:** This process concludes by returning a [Object] value.