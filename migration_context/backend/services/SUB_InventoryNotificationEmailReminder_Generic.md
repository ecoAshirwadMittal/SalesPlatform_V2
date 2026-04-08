# Microflow Detailed Specification: SUB_InventoryNotificationEmailReminder_Generic

### 📥 Inputs (Parameters)
- **$ReminderNotificationHelper** (Type: AuctionUI.ReminderNotificationHelper)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
2. 🔀 **DECISION:** `$EcoATMDirectUser/Inactive`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_CreateActivateUserURL** (Result: **$URL_Inactive**)**
      2. **Call Microflow **AuctionUI.SUB_DateTimeEmailBody** (Result: **$DateTime_Inactive**)**
      3. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject_Inactive**)
      - Set **PageURL** = `$URL_Inactive`
      - Set **Name** = `$ReminderNotificationHelper/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **DateTime** = `$DateTime_Inactive`**
      4. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='Inventory_Notification_Reminder']` (Result: **$EmailTemplate_Inactive**)**
      5. **Update **$EmailTemplate_Inactive**
      - Set **To** = `$ReminderNotificationHelper/Email`**
      6. **JavaCallAction**
      7. **Delete**
      8. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **JavaCallAction**
      2. **Create Variable **$URL** = `$RootURL+'/p/buyercodeselect'`**
      3. **Create Variable **$URL_backup** = `$RootURL+'/p/buyercodeselect'`**
      4. **Create Variable **$StyledURL** = `'<a href="'+ $URL+'" target="_blank" style="color:green;">Click here to start bidding.</a>'`**
      5. **Call Microflow **AuctionUI.SUB_DateTimeEmailBody** (Result: **$DateTime**)**
      6. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject**)
      - Set **PageURL** = `$URL`
      - Set **Name** = `$ReminderNotificationHelper/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **DateTime** = `$DateTime`**
      7. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='Inventory_Notification_Reminder']` (Result: **$EmailTemplate**)**
      8. **Update **$EmailTemplate**
      - Set **To** = `$ReminderNotificationHelper/Email`**
      9. **JavaCallAction**
      10. **Delete**
      11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.