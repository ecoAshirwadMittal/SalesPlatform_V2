# Microflow Detailed Specification: BCo_MxObjectReference

### 📥 Inputs (Parameters)
- **$MxObjectReference** (Type: MxModelReflection.MxObjectReference)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$MxObjectReference/ReadableName != empty and $MxObjectReference/ReadableName != ''`
   ➔ **If [false]:**
      1. **Update **$MxObjectReference**
      - Set **ReadableName** = `$MxObjectReference/CompleteName`**
      2. 🏁 **END:** Return `true`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.