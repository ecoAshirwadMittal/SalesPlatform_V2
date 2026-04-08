# Microflow Detailed Specification: VAL_Buyer

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. 🔀 **DECISION:** `trim($Buyer/CompanyName)!=''`
   ➔ **If [true]:**
      1. **Update **$Buyer**
      - Set **BuyerEmptyValidationMessage** = `empty`**
      2. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[CompanyName=$Buyer/CompanyName] [id!=$Buyer]` (Result: **$ExistingBuyer**)**
      3. 🔀 **DECISION:** `$ExistingBuyer=empty`
         ➔ **If [true]:**
            1. **Update **$Buyer**
      - Set **BuyerUniqueValidationMessage** = `empty`**
            2. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList**)**
            3. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BuyerCodeList_Delete**)**
            4. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_toCommit**)**
            5. 🔀 **DECISION:** `$BuyerCodeList_toCommit!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `'Atleast one buyer code should be added to buyer before saving'`**
                  3. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **Update **$Buyer**
      - Set **BuyerUniqueValidationMessage** = `'buyer name already exists'`**
            3. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList**)**
            4. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BuyerCodeList_Delete**)**
            5. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_toCommit**)**
            6. 🔀 **DECISION:** `$BuyerCodeList_toCommit!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `'Atleast one buyer code should be added to buyer before saving'`**
                  3. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. **Update Variable **$isValid** = `false`**
      2. **Update **$Buyer**
      - Set **BuyerEmptyValidationMessage** = `'add buyer name'`**
      3. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[CompanyName=$Buyer/CompanyName] [id!=$Buyer]` (Result: **$ExistingBuyer**)**
      4. 🔀 **DECISION:** `$ExistingBuyer=empty`
         ➔ **If [true]:**
            1. **Update **$Buyer**
      - Set **BuyerUniqueValidationMessage** = `empty`**
            2. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList**)**
            3. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BuyerCodeList_Delete**)**
            4. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_toCommit**)**
            5. 🔀 **DECISION:** `$BuyerCodeList_toCommit!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `'Atleast one buyer code should be added to buyer before saving'`**
                  3. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **Update **$Buyer**
      - Set **BuyerUniqueValidationMessage** = `'buyer name already exists'`**
            3. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList**)**
            4. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BuyerCodeList_Delete**)**
            5. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_toCommit**)**
            6. 🔀 **DECISION:** `$BuyerCodeList_toCommit!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `'Atleast one buyer code should be added to buyer before saving'`**
                  3. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. **Update **$Buyer**
      - Set **BuyerCodesEmptyValidationMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.