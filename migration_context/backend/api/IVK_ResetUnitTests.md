# Microflow Detailed Specification: IVK_ResetUnitTests

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **UnitTesting.TestSuite**  (Result: **$TestSuiteList**)**
2. 🔄 **LOOP:** For each **$IteratorTestSuite** in **$TestSuiteList**
   │ 1. **Update **$IteratorTestSuite**
      - Set **LastRun** = `empty`
      - Set **TestFailedCount** = `0`
      - Set **Result** = `empty`**
   └─ **End Loop**
3. **Commit/Save **$TestSuiteList** to Database**
4. **DB Retrieve **UnitTesting.UnitTest**  (Result: **$UnitTestList**)**
5. 🔄 **LOOP:** For each **$IteratorUnitTest** in **$UnitTestList**
   │ 1. **Update **$IteratorUnitTest**
      - Set **Result** = `empty`
      - Set **ResultMessage** = `empty`
      - Set **LastStep** = `empty`
      - Set **LastRun** = `empty`
      - Set **ReadableTime** = `'-'`**
   └─ **End Loop**
6. **Commit/Save **$UnitTestList** to Database**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.