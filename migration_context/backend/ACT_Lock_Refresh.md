# Microflow Detailed Specification: ACT_Lock_Refresh

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo!=empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Close current page/popup**
            2. **Show Message (Warning): `This page is already used by {1}`**
            3. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.