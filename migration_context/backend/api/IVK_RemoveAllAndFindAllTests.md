# Microflow Detailed Specification: IVK_RemoveAllAndFindAllTests

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **UnitTesting.TestSuite**  (Result: **$TestSuiteList**)**
2. **Delete**
3. **Call Microflow **UnitTesting.IVK_FindAllTests****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.