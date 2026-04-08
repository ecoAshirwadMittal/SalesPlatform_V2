# Microflow Detailed Specification: ACT_SaveDocument

### 📥 Inputs (Parameters)
- **$UserHelperGuide** (Type: EcoATM_MDM.UserHelperGuide)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_MDM.UserHelperGuide** Filter: `[GuideType=$UserHelperGuide/GuideType] [id!=$UserHelperGuide] [Active=$UserHelperGuide/Active]` (Result: **$DuplicateGuideDocument**)**
2. 🔀 **DECISION:** `$DuplicateGuideDocument!=empty`
   ➔ **If [false]:**
      1. **Commit/Save **$UserHelperGuide** to Database**
      2. 🔀 **DECISION:** `$UserHelperGuide/GuideType != AuctionUI.ENUM_DocumentType.Sales_Platform_Terms`
         ➔ **If [true]:**
            1. **Close current page/popup**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **AuctionUI.SUB_ResetUserAcknowledgement****
            2. **Close current page/popup**
            3. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Show Message (Warning): `User help guide exists, kindly edit existing document.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.