# Microflow Detailed Specification: MB_TestTokenPattern

### 📥 Inputs (Parameters)
- **$Token** (Type: MxModelReflection.Token)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **MxModelReflection.TestPattern** (Result: **$NewTestPattern**)
      - Set **DisplayPattern** = `$Token/DisplayPattern`**
2. **Maps to Page: **MxModelReflection.TestPattern_Edit****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.