# Microflow Detailed Specification: Sub_PerformRestoreActivity

### 📥 Inputs (Parameters)
- **$AppUrl** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[not (contains(Email,'ecoatm'))]` (Result: **$NonEcoATMUsers**)**
3. **Delete**
4. **DB Retrieve **SAML20.SSOConfiguration** Filter: `[Active]` (Result: **$CurrentActiveSaml**)**
5. **Update **$CurrentActiveSaml** (and Save to DB)
      - Set **Active** = `false`**
6. **DB Retrieve **SAML20.SSOConfiguration** Filter: `[Alias='QA Config']` (Result: **$RequiredSAMLForEnvironment**)**
7. 🔀 **DECISION:** `$RequiredSAMLForEnvironment!=empty`
   ➔ **If [true]:**
      1. **Update **$RequiredSAMLForEnvironment** (and Save to DB)
      - Set **Active** = `true`**
      2. **CreateList**
      3. **CreateList**
      4. **CreateList**
      5. **Create Variable **$UsersCount** = `@AuctionUI.TestUsersCount`**
      6. **DB Retrieve **System.UserRole** Filter: `[Name='Bidder']` (Result: **$BidderUserRole**)**
      7. 🔄 **LOOP:** For each **$undefined** in **$undefined**
         │ 1. **Call Microflow **AuctionUI.ACT_CreateNewUsers_TestEnvironment****
         │ 2. **Update Variable **$UsersCount** = `$UsersCount-1`**
         └─ **End Loop**
      8. **Commit/Save **$BuyerList** to Database**
      9. **Commit/Save **$BuyerCodeList** to Database**
      10. **Commit/Save **$EcoATMDirectUserList** to Database**
      11. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[Running]` (Result: **$RunningSchedules**)**
      12. 🔄 **LOOP:** For each **$IteratorSchedule** in **$RunningSchedules**
         │ 1. **Update **$IteratorSchedule**
      - Set **Running** = `false`**
         └─ **End Loop**
      13. **Commit/Save **$RunningSchedules** to Database**
      14. **DB Retrieve **MicrosoftGraph.Authentication** Filter: `[IsActive]` (Result: **$ActiveSharepointAuthentication**)**
      15. **Update **$ActiveSharepointAuthentication** (and Save to DB)
      - Set **IsActive** = `false`**
      16. **DB Retrieve **MicrosoftGraph.Authentication** Filter: `[DisplayName='QA']` (Result: **$RequiredSharepointAuthentication**)**
      17. 🔀 **DECISION:** `$RequiredSharepointAuthentication!=empty`
         ➔ **If [true]:**
            1. **Update **$RequiredSharepointAuthentication** (and Save to DB)
      - Set **IsActive** = `true`**
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **CreateList**
      2. **CreateList**
      3. **CreateList**
      4. **Create Variable **$UsersCount** = `@AuctionUI.TestUsersCount`**
      5. **DB Retrieve **System.UserRole** Filter: `[Name='Bidder']` (Result: **$BidderUserRole**)**
      6. 🔄 **LOOP:** For each **$undefined** in **$undefined**
         │ 1. **Call Microflow **AuctionUI.ACT_CreateNewUsers_TestEnvironment****
         │ 2. **Update Variable **$UsersCount** = `$UsersCount-1`**
         └─ **End Loop**
      7. **Commit/Save **$BuyerList** to Database**
      8. **Commit/Save **$BuyerCodeList** to Database**
      9. **Commit/Save **$EcoATMDirectUserList** to Database**
      10. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[Running]` (Result: **$RunningSchedules**)**
      11. 🔄 **LOOP:** For each **$IteratorSchedule** in **$RunningSchedules**
         │ 1. **Update **$IteratorSchedule**
      - Set **Running** = `false`**
         └─ **End Loop**
      12. **Commit/Save **$RunningSchedules** to Database**
      13. **DB Retrieve **MicrosoftGraph.Authentication** Filter: `[IsActive]` (Result: **$ActiveSharepointAuthentication**)**
      14. **Update **$ActiveSharepointAuthentication** (and Save to DB)
      - Set **IsActive** = `false`**
      15. **DB Retrieve **MicrosoftGraph.Authentication** Filter: `[DisplayName='QA']` (Result: **$RequiredSharepointAuthentication**)**
      16. 🔀 **DECISION:** `$RequiredSharepointAuthentication!=empty`
         ➔ **If [true]:**
            1. **Update **$RequiredSharepointAuthentication** (and Save to DB)
      - Set **IsActive** = `true`**
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.