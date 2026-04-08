# Microflow Detailed Specification: SUB_InventoryNotificationEmailStart_Round_3

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_GetSharepointBidRoundURL** (Result: **$BidSheet_Url**)**
2. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
3. 🔀 **DECISION:** `$EcoATMDirectUser/Inactive`
   ➔ **If [false]:**
      1. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject**)
      - Set **Name** = `$EcoATMDirectUser/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **BidSheet_Url** = `$BidSheet_Url`**
      2. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='Inventory_Notification_Start_Round3']` (Result: **$EmailTemplate**)**
      3. **Update **$EmailTemplate**
      - Set **To** = `$EcoATMDirectUser/Name`**
      4. **JavaCallAction**
      5. **Delete**
      6. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_CreateActivateUserURL** (Result: **$URL_Inactive**)**
      2. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject_Inactive**)
      - Set **PageURL** = `$URL_Inactive`
      - Set **Name** = `$EcoATMDirectUser/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **BidSheet_Url** = `$BidSheet_Url`**
      3. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='Inventory_Notification_Start_Round3']` (Result: **$EmailTemplate_Inactive**)**
      4. **Update **$EmailTemplate_Inactive**
      - Set **To** = `$EcoATMDirectUser/Name`**
      5. **JavaCallAction**
      6. **Delete**
      7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.