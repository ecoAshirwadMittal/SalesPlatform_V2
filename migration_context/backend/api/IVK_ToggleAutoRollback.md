# Microflow Detailed Specification: IVK_ToggleAutoRollback

### 📥 Inputs (Parameters)
- **$TestSuite** (Type: UnitTesting.TestSuite)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$TestSuite** (and Save to DB)
      - Set **AutoRollbackMFs** = `not($TestSuite/AutoRollbackMFs)`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.