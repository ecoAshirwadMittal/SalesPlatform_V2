# Microflow Detailed Specification: SUB_InventoryNotificationEmailStart_Generic

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_GetSharepointBidRoundURL** (Result: **$BidSheet_Url**)**
2. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
3. 🔀 **DECISION:** `$EcoATMDirectUser/OverallUserStatus`
   ➔ **If [Active]:**
      1. **JavaCallAction**
      2. **Create Variable **$URL** = `$RootURL+'/p/buyercodeselect'`**
      3. **Create Variable **$StyledURL** = `'<a href="'+ $URL+'" target="_blank" style="color:green;">Click here to start bidding.</a>'`**
      4. **Call Microflow **AuctionUI.SUB_DateTimeEmailBody** (Result: **$DateTime**)**
      5. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject**)
      - Set **PageURL** = `$URL`
      - Set **Name** = `$EcoATMDirectUser/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **DateTime** = `$DateTime`**
      6. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='Inventory_Notification_Start']` (Result: **$EmailTemplate**)**
      7. **Update **$EmailTemplate**
      - Set **To** = `$EcoATMDirectUser/Name`**
      8. **JavaCallAction**
      9. **Delete**
      10. 🏁 **END:** Return empty
   ➔ **If [Inactive]:**
      1. **Call Microflow **AuctionUI.ACT_CreateActivateUserURL** (Result: **$URL_Inactive**)**
      2. **Call Microflow **AuctionUI.SUB_DateTimeEmailBody** (Result: **$DateTime_Inactive**)**
      3. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject_Inactive**)
      - Set **PageURL** = `$URL_Inactive`
      - Set **Name** = `$EcoATMDirectUser/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **DateTime** = `$DateTime_Inactive`**
      4. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='Inventory_Notification_Start']` (Result: **$EmailTemplate_Inactive**)**
      5. **Update **$EmailTemplate_Inactive**
      - Set **To** = `$EcoATMDirectUser/Name`**
      6. **JavaCallAction**
      7. **Delete**
      8. 🏁 **END:** Return empty
   ➔ **If [Disabled]:**
      1. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.