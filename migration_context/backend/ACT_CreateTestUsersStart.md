# Microflow Detailed Specification: ACT_CreateTestUsersStart

### 📥 Inputs (Parameters)
- **$TestUserHelper** (Type: EcoATM_UserManagement.TestUserHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'TestUserCreation'`**
2. **Create Variable **$Description** = `'Creating Test Users for Automation'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. 🔀 **DECISION:** `$TestUserHelper/From<=$TestUserHelper/To`
   ➔ **If [true]:**
      1. **CreateList**
      2. **CreateList**
      3. **CreateList**
      4. 🔄 **LOOP:** For each **$undefined** in **$undefined**
         │ 1. **Create Variable **$Email** = `'ecoauc.automation'+$TestUserHelper/From+'@anything.com'`**
         │ 2. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name = $Email]` (Result: **$EcoATMDirectUser**)**
         │ 3. 🔀 **DECISION:** `$EcoATMDirectUser=empty`
         │    ➔ **If [true]:**
         │       1. **Create **EcoATM_BuyerManagement.Buyer** (Result: **$NewBuyer**)
      - Set **CompanyName** = `'Auction Automation '+$TestUserHelper/From`
      - Set **Status** = `AuctionUI.enum_BuyerStatus.Active`
      - Set **EntityOwner** = `$currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`**
         │       2. **Create **EcoATM_BuyerManagement.BuyerCode** (Result: **$NewBuyerCode**)
      - Set **BuyerCodeType** = `AuctionUI.enum_BuyerCodeType.Wholesale`
      - Set **Code** = `'AA'+$TestUserHelper/From+'WHL'`
      - Set **Status** = `AuctionUI.enum_BuyerCodeStatus.Active`
      - Set **EntityOwner** = `$currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`
      - Set **BuyerCode_Buyer** = `$NewBuyer`**
         │       3. **Update **$NewBuyer**
      - Set **BuyerCodesDisplay** = `$NewBuyerCode/Code`**
         │       4. **Add **$$NewBuyer** to/from list **$BuyerList****
         │       5. **Add **$$NewBuyerCode** to/from list **$BuyerCodeList****
         │       6. **DB Retrieve **System.Language** Filter: `[Code = 'en_US']` (Result: **$Language**)**
         │       7. **DB Retrieve **System.UserRole** Filter: `[Name = 'Bidder']` (Result: **$UserRole_Bidder**)**
         │       8. **Create **EcoATM_UserManagement.EcoATMDirectUser** (Result: **$NewEcoATMDirectUser**)
      - Set **LandingPagePreference** = `EcoATM_UserManagement.ENUM_LandingPagePreference.Wholesale_Auction`
      - Set **EntityOwner** = `$currentUser/Name`
      - Set **EntityChanger** = `$currentUser/Name`
      - Set **UserRolesDisplay** = `$UserRole_Bidder/Name`
      - Set **UserBuyerDisplay** = `$NewBuyer/CompanyName`
      - Set **FirstName** = `'ecoauc.automation'+$TestUserHelper/From`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`
      - Set **FullName** = `'ecoauc.automation'+$TestUserHelper/From`
      - Set **Email** = `$Email`
      - Set **IsLocalUser** = `true`
      - Set **Name** = `$Email`
      - Set **Password** = `'ecoATM123$'`
      - Set **EcoATMDirectUser_Buyer** = `$BuyerList`
      - Set **User_Language** = `$Language`
      - Set **UserRoles** = `$UserRole_Bidder`
      - Set **ActivationDate** = `[%CurrentDateTime%]`**
         │       9. **Add **$$NewEcoATMDirectUser** to/from list **$EcoATMDirectUserList****
         │       10. **Update **$TestUserHelper**
      - Set **From** = `$TestUserHelper/From+1`**
         │    ➔ **If [false]:**
         │       1. **Update **$TestUserHelper**
      - Set **From** = `$TestUserHelper/From+1`**
         └─ **End Loop**
      5. 🔀 **DECISION:** `$EcoATMDirectUserList!=empty`
         ➔ **If [true]:**
            1. **Commit/Save **$BuyerCodeList** to Database**
            2. **Commit/Save **$BuyerList** to Database**
            3. **Commit/Save **$EcoATMDirectUserList** to Database**
            4. **Close current page/popup**
            5. **Show Message (Information): `Automation users successfully created!`**
            6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            7. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Close current page/popup**
            2. **Show Message (Information): `Automation users within the given range already exist!`**
            3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.