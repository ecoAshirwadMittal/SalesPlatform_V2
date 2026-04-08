# Microflow Detailed Specification: SUB_SendSubmitBidConfirmationEmail

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
2. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$SchedulingAuction**)**
3. **Call Microflow **AuctionUI.SUB_DateTimeEmailBody** (Result: **$DateTime**)**
4. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[id=$currentUser]` (Result: **$EcoATMDirectUser**)**
5. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
6. **List Operation: **Find** on **$undefined** where `'Bidder'` (Result: **$Bidder**)**
7. 🔀 **DECISION:** `$Bidder!=empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject**)
      - Set **Name** = `$EcoATMDirectUser/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **DateTime** = `$DateTime`**
      2. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='SubmitBids_Confirmation']` (Result: **$EmailTemplate**)**
      3. **Update **$EmailTemplate**
      - Set **To** = `$EcoATMDirectUser/Name`**
      4. **JavaCallAction**
      5. **Delete**
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Retrieve related **BidRound_BuyerCode** via Association from **$BidRound** (Result: **$BuyerCode**)**
      2. **Retrieve related **BuyerCode_Buyer** via Association from **$BuyerCode** (Result: **$Buyer**)**
      3. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer** (Result: **$EcoATMDirectUserList**)**
      4. **List Operation: **Filter** on **$undefined** where `false` (Result: **$ActiveEcoATMDirectUserList**)**
      5. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[TemplateName='SubmitBids_Confirmation_Admin']` (Result: **$EmailTemplate_Admin**)**
      6. **JavaCallAction**
      7. **Create Variable **$URL** = `$RootURL+'/p/buyercodeselect'`**
      8. 🔄 **LOOP:** For each **$IteratorEcoATMDirectUser** in **$ActiveEcoATMDirectUserList**
         │ 1. **Create **AuctionUI.EmailNotification** (Result: **$LinkObject_1**)
      - Set **Name** = `$IteratorEcoATMDirectUser/FirstName`
      - Set **Round** = `$SchedulingAuction/Round`
      - Set **Auction** = `$SchedulingAuction/Auction_Week_Year`
      - Set **DateTime** = `$DateTime`
      - Set **Note** = `$BidRound/Note`
      - Set **PageURL** = `$URL`**
         │ 2. **Update **$EmailTemplate_Admin**
      - Set **To** = `$IteratorEcoATMDirectUser/Name`**
         │ 3. **JavaCallAction**
         │ 4. **Delete**
         └─ **End Loop**
      9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.