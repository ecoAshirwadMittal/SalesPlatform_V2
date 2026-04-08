# Microflow Detailed Specification: BCo_MxObjectMember_CreateCompleteMemberName

### 📥 Inputs (Parameters)
- **$MxObjectMember** (Type: MxModelReflection.MxObjectMember)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxObjectMember_MxObjectType** via Association from **$MxObjectMember** (Result: **$MxObjectType**)**
2. 🔀 **DECISION:** `$MxObjectType != empty`
   ➔ **If [true]:**
      1. **Update **$MxObjectMember**
      - Set **CompleteName** = `$MxObjectType/CompleteName + ' / ' + $MxObjectMember/AttributeName`**
      2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Update **$MxObjectMember**
      - Set **CompleteName** = `' / ' + $MxObjectMember/AttributeName`**
      2. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.