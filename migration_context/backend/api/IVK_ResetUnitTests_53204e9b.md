# Microflow Analysis: IVK_ResetUnitTests

### Execution Steps:
1. **Search the Database for **UnitTesting.TestSuite** using filter: { Show everything } (Call this list **$TestSuiteList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Search the Database for **UnitTesting.UnitTest** using filter: { Show everything } (Call this list **$UnitTestList**)**
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
