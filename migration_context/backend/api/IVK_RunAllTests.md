# Microflow Detailed Specification: IVK_RunAllTests

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **DB Retrieve **UnitTesting.TestSuite**  (Result: **$TestSuiteList**)**
3. **Commit/Save **$TestSuiteList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.