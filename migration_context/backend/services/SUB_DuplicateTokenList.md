# Microflow Detailed Specification: SUB_DuplicateTokenList

### 📥 Inputs (Parameters)
- **$TokenList** (Type: MxModelReflection.Token)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$Iterator** in **$TokenList**
   │ 1. **Create **MxModelReflection.Token** (Result: **$NewToken**)**
   │ 2. **Update **$NewToken**
      - Set **Token** = `$Iterator/Token`
      - Set **TokenType** = `$Iterator/TokenType`
      - Set **Prefix** = `$Iterator/Prefix`
      - Set **Suffix** = `$Iterator/Suffix`
      - Set **CombinedToken** = `$Iterator/CombinedToken`
      - Set **MetaModelPath** = `$Iterator/MetaModelPath`
      - Set **Status** = `$Iterator/Status`
      - Set **FindObjectStart** = `$Iterator/FindObjectStart`
      - Set **FindObjectReference** = `$Iterator/FindObjectReference`
      - Set **FindReference** = `$Iterator/FindReference`
      - Set **FindMember** = `$Iterator/FindMember`
      - Set **FindMemberReference** = `$Iterator/FindMemberReference`
      - Set **IsOptional** = `$Iterator/IsOptional`
      - Set **Token_MxObjectMember** = `$Iterator/MxModelReflection.Token_MxObjectMember`
      - Set **Token_MxObjectType_Start** = `$Iterator/MxModelReflection.Token_MxObjectType_Start`
      - Set **Token_MxObjectType_Referenced** = `$Iterator/MxModelReflection.Token_MxObjectType_Referenced`
      - Set **Token_MxObjectReference** = `$Iterator/MxModelReflection.Token_MxObjectReference`**
   │ 3. **Add **$$NewToken** to/from list **$NewTokenList****
   └─ **End Loop**
3. 🏁 **END:** Return `$NewTokenList`

**Final Result:** This process concludes by returning a [List] value.