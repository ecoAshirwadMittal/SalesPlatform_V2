# Microflow Detailed Specification: ACT_OpenBuyerEditPage

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BuyerCode_Buyer** via Association from **$Buyer** (Result: **$BuyerCodeList**)**
2. **List Operation: **Filter** on **$undefined** where `true` (Result: **$SoftDeleteList**)**
3. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$SoftDeleteList**
   │ 1. **Update **$IteratorBuyerCode**
      - Set **softDelete** = `false`**
   └─ **End Loop**
4. **Retrieve related **UserRoles** via Association from **$currentUser** (Result: **$UserRoleList**)**
5. **List Operation: **Find** on **$undefined** where `'Compliance'` (Result: **$ComplianceRole**)**
6. **List Operation: **Find** on **$undefined** where `'Administrator'` (Result: **$AdministratorRole**)**
7. 🔀 **DECISION:** `$ComplianceRole = empty`
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_MDM.Buyer_Edit****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_MDM.Buyer_Edit_Compliance****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.