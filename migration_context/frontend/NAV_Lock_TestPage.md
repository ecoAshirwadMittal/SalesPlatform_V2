# Microflow Detailed Specification: NAV_Lock_TestPage

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.Offer**  (Result: **$Offer**)**
2. **JavaCallAction**
3. 🔀 **DECISION:** `$ObjectInfo!=empty and $ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_Lock.Lock_Test****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Sorry, this information is currently modified by {1}.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.