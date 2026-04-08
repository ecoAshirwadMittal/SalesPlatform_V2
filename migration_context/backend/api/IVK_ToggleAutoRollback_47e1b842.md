# Microflow Analysis: IVK_ToggleAutoRollback

### Requirements (Inputs):
- **$TestSuite** (A record of type: UnitTesting.TestSuite)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [UnitTesting.TestSuite.AutoRollbackMFs] to: "not($TestSuite/AutoRollbackMFs)"
      - **Save:** This change will be saved to the database immediately.**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
