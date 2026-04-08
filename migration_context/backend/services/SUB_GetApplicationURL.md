# Microflow Detailed Specification: SUB_GetApplicationURL

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `endsWith($url,'/')`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$url`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$url + '/'`

**Final Result:** This process concludes by returning a [String] value.