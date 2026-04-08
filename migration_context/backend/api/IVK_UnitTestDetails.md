# Microflow Detailed Specification: IVK_UnitTestDetails

### 📥 Inputs (Parameters)
- **$UnitTest** (Type: UnitTesting.UnitTest)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$UnitTest/IsMf`
   ➔ **If [true]:**
      1. **Maps to Page: **UnitTesting.UnitTestRun_Details****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **UnitTesting.UnitTestRun_Details****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.