# Microflow Detailed Specification: ACT_Round3_StartNotification

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[ ( System.UserRoles = '[%UserRole_SalesRep%]' or System.UserRoles = '[%UserRole_SalesOps%]' ) ]` (Result: **$SalesRepOpsList**)**
2. 🔄 **LOOP:** For each **$IteratorAccountStart** in **$SalesRepOpsList**
   │ 1. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailStart_Round_3****
   └─ **End Loop**
3. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isStartNotificationSent** = `true`**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.