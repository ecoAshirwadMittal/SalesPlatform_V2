# Microflow Detailed Specification: VAL_BuyerCode

### 📥 Inputs (Parameters)
- **$BuyerCodeList** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │ 1. 🔀 **DECISION:** `trim($IteratorBuyerCode/Code)!=''`
   │    ➔ **If [false]:**
   │       1. **Update Variable **$isValid** = `false`**
   │       2. **Update **$IteratorBuyerCode**
      - Set **codeEmptyValid** = `false`**
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorBuyerCode**
      - Set **codeEmptyValid** = `true`**
   └─ **End Loop**
3. **List Operation: **Filter** on **$undefined** where `false` (Result: **$BuyerCode_CodeEmptyValid**)**
4. 🔀 **DECISION:** `$BuyerCode_CodeEmptyValid=empty`
   ➔ **If [true]:**
      1. **Update **$Buyer**
      - Set **buyerCodeInvalidMessage_empty** = `empty`**
      2. 🔄 **LOOP:** For each **$IteratorBuyerCode_1** in **$BuyerCodeList**
         │ 1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Code=$IteratorBuyerCode_1/Code] [id!=$IteratorBuyerCode_1]` (Result: **$ExistingBuyerCode**)**
         │ 2. 🔀 **DECISION:** `$ExistingBuyerCode=empty`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorBuyerCode_1**
      - Set **codeUniqueValid** = `true`**
         │    ➔ **If [false]:**
         │       1. **Update Variable **$isValid** = `false`**
         │       2. **Update **$IteratorBuyerCode_1**
      - Set **codeUniqueValid** = `false`**
         └─ **End Loop**
      3. **List Operation: **Filter** on **$undefined** where `false` (Result: **$BuyerCde_CodeUniqueValid**)**
      4. 🔀 **DECISION:** `$BuyerCde_CodeUniqueValid=empty`
         ➔ **If [true]:**
            1. **Update **$Buyer**
      - Set **buyerCodeInvalidMessage_Unique** = `empty`**
            2. 🔄 **LOOP:** For each **$IteratorBuyerCode_2** in **$BuyerCodeList**
               │ 1. 🔀 **DECISION:** `$IteratorBuyerCode_2/BuyerCodeType!=empty`
               │    ➔ **If [false]:**
               │       1. **Update Variable **$isValid** = `false`**
               │       2. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `false`**
               │    ➔ **If [true]:**
               │       1. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `true`**
               └─ **End Loop**
            3. **List Operation: **Filter** on **$undefined** where `false` (Result: **$BuyeCode_TypeValid**)**
            4. 🔀 **DECISION:** `$BuyeCode_TypeValid=empty`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `'Buyer code type cannot be empty'`**
                  2. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update **$Buyer**
      - Set **buyerCodeInvalidMessage_Unique** = `'This buyer code already exists'`**
            2. 🔄 **LOOP:** For each **$IteratorBuyerCode_2** in **$BuyerCodeList**
               │ 1. 🔀 **DECISION:** `$IteratorBuyerCode_2/BuyerCodeType!=empty`
               │    ➔ **If [false]:**
               │       1. **Update Variable **$isValid** = `false`**
               │       2. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `false`**
               │    ➔ **If [true]:**
               │       1. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `true`**
               └─ **End Loop**
            3. **List Operation: **Filter** on **$undefined** where `false` (Result: **$BuyeCode_TypeValid**)**
            4. 🔀 **DECISION:** `$BuyeCode_TypeValid=empty`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `'Buyer code type cannot be empty'`**
                  2. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. **Update **$Buyer**
      - Set **buyerCodeInvalidMessage_empty** = `'Buyer code cannot be empty'`**
      2. 🔄 **LOOP:** For each **$IteratorBuyerCode_1** in **$BuyerCodeList**
         │ 1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Code=$IteratorBuyerCode_1/Code] [id!=$IteratorBuyerCode_1]` (Result: **$ExistingBuyerCode**)**
         │ 2. 🔀 **DECISION:** `$ExistingBuyerCode=empty`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorBuyerCode_1**
      - Set **codeUniqueValid** = `true`**
         │    ➔ **If [false]:**
         │       1. **Update Variable **$isValid** = `false`**
         │       2. **Update **$IteratorBuyerCode_1**
      - Set **codeUniqueValid** = `false`**
         └─ **End Loop**
      3. **List Operation: **Filter** on **$undefined** where `false` (Result: **$BuyerCde_CodeUniqueValid**)**
      4. 🔀 **DECISION:** `$BuyerCde_CodeUniqueValid=empty`
         ➔ **If [true]:**
            1. **Update **$Buyer**
      - Set **buyerCodeInvalidMessage_Unique** = `empty`**
            2. 🔄 **LOOP:** For each **$IteratorBuyerCode_2** in **$BuyerCodeList**
               │ 1. 🔀 **DECISION:** `$IteratorBuyerCode_2/BuyerCodeType!=empty`
               │    ➔ **If [false]:**
               │       1. **Update Variable **$isValid** = `false`**
               │       2. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `false`**
               │    ➔ **If [true]:**
               │       1. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `true`**
               └─ **End Loop**
            3. **List Operation: **Filter** on **$undefined** where `false` (Result: **$BuyeCode_TypeValid**)**
            4. 🔀 **DECISION:** `$BuyeCode_TypeValid=empty`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `'Buyer code type cannot be empty'`**
                  2. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update **$Buyer**
      - Set **buyerCodeInvalidMessage_Unique** = `'This buyer code already exists'`**
            2. 🔄 **LOOP:** For each **$IteratorBuyerCode_2** in **$BuyerCodeList**
               │ 1. 🔀 **DECISION:** `$IteratorBuyerCode_2/BuyerCodeType!=empty`
               │    ➔ **If [false]:**
               │       1. **Update Variable **$isValid** = `false`**
               │       2. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `false`**
               │    ➔ **If [true]:**
               │       1. **Update **$IteratorBuyerCode_2**
      - Set **typevalid** = `true`**
               └─ **End Loop**
            3. **List Operation: **Filter** on **$undefined** where `false` (Result: **$BuyeCode_TypeValid**)**
            4. 🔀 **DECISION:** `$BuyeCode_TypeValid=empty`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update **$Buyer**
      - Set **buyerCodeTypeInvalidMessage** = `'Buyer code type cannot be empty'`**
                  2. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.