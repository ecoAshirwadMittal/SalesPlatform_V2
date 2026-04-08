# Microflow Detailed Specification: BCo_Token

### 📥 Inputs (Parameters)
- **$Token** (Type: MxModelReflection.Token)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Token**
      - Set **CombinedToken** = `$Token/Prefix + $Token/Token + $Token/Suffix`
      - Set **Description** = `if $Token/Description = empty or $Token/Description = '' then $Token/Token else $Token/Description`**
2. **Retrieve related **Token_MxObjectType_Start** via Association from **$Token** (Result: **$MxObjectTypeStart**)**
3. 🔀 **DECISION:** `$Token/TokenType`
   ➔ **If [Attribute]:**
      1. **Retrieve related **Token_MxObjectMember** via Association from **$Token** (Result: **$Member**)**
      2. 🔀 **DECISION:** `$Member != empty and $MxObjectTypeStart != empty`
         ➔ **If [true]:**
            1. **Update **$Token**
      - Set **Status** = `MxModelReflection.Status.Valid`
      - Set **MetaModelPath** = `$MxObjectTypeStart/CompleteName + '/' + $Member/AttributeName`**
            2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Update **$Token**
      - Set **Status** = `MxModelReflection.Status.Invalid`**
            2. 🏁 **END:** Return `true`
   ➔ **If [(empty)]:**
      1. **Update **$Token**
      - Set **Status** = `MxModelReflection.Status.Invalid`**
      2. 🏁 **END:** Return `true`
   ➔ **If [Reference]:**
      1. **Retrieve related **Token_MxObjectReference** via Association from **$Token** (Result: **$MxObjectReference**)**
      2. **Retrieve related **Token_MxObjectType_Referenced** via Association from **$Token** (Result: **$MxObjectTypeReference**)**
      3. **Retrieve related **Token_MxObjectMember** via Association from **$Token** (Result: **$MemberReference**)**
      4. 🔀 **DECISION:** `$MemberReference != empty and $MxObjectReference != empty and $MxObjectTypeStart != empty and $MxObjectTypeReference != empty`
         ➔ **If [false]:**
            1. **Update **$Token**
      - Set **Status** = `MxModelReflection.Status.Invalid`**
            2. 🏁 **END:** Return `true`
         ➔ **If [true]:**
            1. **Update **$Token**
      - Set **Status** = `MxModelReflection.Status.Valid`
      - Set **MetaModelPath** = `$MxObjectTypeStart/CompleteName + '/' + $MxObjectReference/CompleteName + '/' + $MxObjectTypeReference/CompleteName + '/' + $MemberReference/AttributeName`**
            2. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.