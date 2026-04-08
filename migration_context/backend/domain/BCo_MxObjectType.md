# Microflow Detailed Specification: BCo_MxObjectType

### 📥 Inputs (Parameters)
- **$MxObjectType** (Type: MxModelReflection.MxObjectType)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$MxObjectType/ReadableName != empty and $MxObjectType/ReadableName != ''`
   ➔ **If [false]:**
      1. **Update **$MxObjectType**
      - Set **ReadableName** = `$MxObjectType/Name + ' from the ' + $MxObjectType/Module + ' module'`**
      2. 🏁 **END:** Return `true`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.