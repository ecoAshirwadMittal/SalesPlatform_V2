# Microflow Detailed Specification: ACT_Emailtemplate_CreateToken

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailTemplate/Email_Connector.EmailTemplate_MxObjectType != empty`
   ➔ **If [true]:**
      1. **Retrieve related **EmailTemplate_Token** via Association from **$EmailTemplate** (Result: **$TokenList**)**
      2. **Create Variable **$IteratorCheck** = `false`**
      3. 🔄 **LOOP:** For each **$Tokens** in **$TokenList**
         │ 1. **Retrieve related **Token_MxObjectType_Start** via Association from **$Tokens** (Result: **$MxObjectType**)**
         │ 2. 🔀 **DECISION:** `$MxObjectType = $EmailTemplate/Email_Connector.EmailTemplate_MxObjectType`
         │    ➔ **If [true]:**
         │    ➔ **If [false]:**
         │       1. **Show Message (Warning): `Object for the token should be the same object as other tokens.`**
         │       2. **Update Variable **$IteratorCheck** = `true`**
         └─ **End Loop**
      4. 🔀 **DECISION:** `$IteratorCheck`
         ➔ **If [false]:**
            1. **Create **MxModelReflection.Token** (Result: **$NewToken**)
      - Set **Token_MxObjectType_Start** = `$EmailTemplate/Email_Connector.EmailTemplate_MxObjectType`**
            2. **Update **$EmailTemplate**
      - Set **EmailTemplate_Token** = `$NewToken`**
            3. **Maps to Page: **MxModelReflection.Token_NewEdit****
            4. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `There is no object for the token selected yet, this has to be chosen first before new tokens can be created.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.