# Microflow Detailed Specification: SUB_ResetUserAcknowledgement

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser**  (Result: **$EcoATMDirectUserList**)**
2. 🔄 **LOOP:** For each **$IteratorEcoATMDirectUser** in **$EcoATMDirectUserList**
   │ 1. **Update **$IteratorEcoATMDirectUser**
      - Set **Acknowledgement** = `false`**
   └─ **End Loop**
3. **Commit/Save **$EcoATMDirectUserList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.