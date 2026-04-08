# Microflow Detailed Specification: SUB_InvenNotificationProcessDateTime

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$CurrentDT** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$SchedulingAuction/NotificationsEnabled`
   ➔ **If [true]:**
      1. **LogMessage**
      2. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[ ( System.UserRoles = '[%UserRole_Bidder%]' and UserStatus != 'Disabled' ) ]` (Result: **$BidderList**)**
      3. 🔀 **DECISION:** `$SchedulingAuction/isStartNotificationSent`
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🔀 **DECISION:** `$CurrentDT>= trimToMinutes($SchedulingAuction/Start_DateTime)`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round=2`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
                           ➔ **If [false]:**
                              1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'] [ ( BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale' ) ]` (Result: **$BuyerCodeList_StartAuction**)**
                              2. **CreateList**
                              3. 🔄 **LOOP:** For each **$IteratorBuyerCode_StartAuction** in **$BuyerCodeList_StartAuction**
                                 │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyerCode_StartAuction** (Result: **$Buyer_StartAuction**)**
                                 │ 2. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer_StartAuction** (Result: **$BuyerEcoATMDirectUserList_StartAuction**)**
                                 │ 3. **Add **$$BuyerEcoATMDirectUserList_StartAuction** to/from list **$EcoATMDirectUserList_StartAuction****
                                 └─ **End Loop**
                              4. **List Operation: **Union** on **$undefined** (Result: **$DistinctUsers_Start**)**
                              5. 🔄 **LOOP:** For each **$IteratorAccountStart** in **$DistinctUsers_Start**
                                 │ 1. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailStart_Generic****
                                 └─ **End Loop**
                              6. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isStartNotificationSent** = `true`**
                              7. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
                        2. **CreateList**
                        3. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$QualifiedBuyerCodesList_all**)**
                        4. **List Operation: **Filter** on **$undefined** where `true` (Result: **$QualifiedBuyerCodesList_included**)**
                        5. **CreateList**
                        6. 🔄 **LOOP:** For each **$IteratorQualifiedBuyerCodes** in **$QualifiedBuyerCodesList_included**
                           │ 1. **Retrieve related **QualifiedBuyerCodes_BuyerCode** via Association from **$IteratorQualifiedBuyerCodes** (Result: **$BuyerCode**)**
                           │ 2. **Retrieve related **BuyerCode_Buyer** via Association from **$BuyerCode** (Result: **$Buyer**)**
                           │ 3. **Add **$$BuyerCode** to/from list **$BuyerCodeList_Round2****
                           │ 4. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer** (Result: **$EcoATMDirectUserList**)**
                           │ 5. **Add **$$EcoATMDirectUserList** to/from list **$EcoATMDirectUserList_Round2****
                           └─ **End Loop**
                        7. **List Operation: **Union** on **$undefined** (Result: **$EcoATMDirectUserList_Round2_Distinct**)**
                        8. 🔄 **LOOP:** For each **$IteratorAccount_Round2** in **$EcoATMDirectUserList_Round2_Distinct**
                           │ 1. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailStart_Round_2****
                           └─ **End Loop**
                        9. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isStartNotificationSent** = `true`**
                        10. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$SchedulingAuction/EmailReminders = AuctionUI.ENUM_ReminderEmails.AllSent`
               ➔ **If [false]:**
                  1. **Create Variable **$NotificationMinutes_1** = `@AuctionUI.const_AuctionNotificationOneMinutes`**
                  2. 🔀 **DECISION:** `$CurrentDT= subtractMinutes($SchedulingAuction/End_DateTime,$NotificationMinutes_1)`
                     ➔ **If [false]:**
                        1. **Create Variable **$NotificationMinutes_2** = `@AuctionUI.const_AuctionNotificationTwoMinutes`**
                        2. 🔀 **DECISION:** `$CurrentDT= subtractMinutes($SchedulingAuction/End_DateTime,$NotificationMinutes_2)`
                           ➔ **If [false]:**
                              1. **Create Variable **$NotificationMinutes_3** = `@AuctionUI.const_AuctionNotificationThreeMinutes`**
                              2. 🔀 **DECISION:** `$CurrentDT= subtractMinutes($SchedulingAuction/End_DateTime,$NotificationMinutes_3)`
                                 ➔ **If [false]:**
                                    1. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$SchedulingAuction** (and Save to DB)
      - Set **EmailReminders** = `AuctionUI.ENUM_ReminderEmails.AllSent`**
                                    2. 🔀 **DECISION:** `$SchedulingAuction/Round=2`
                                       ➔ **If [false]:**
                                          1. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
                                             ➔ **If [false]:**
                                                1. **CreateList**
                                                2. 🔄 **LOOP:** For each **$IteratorAccountReminder** in **$BidderList**
                                                   │ 1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$IteratorAccountReminder** (Result: **$BuyerList**)**
                                                   │ 2. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
                                                   │    │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
                                                   │    │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe or $currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale` (Result: **$BuyerCodeList_DWandWholesale**)**
                                                   │    │ 3. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList_DWandWholesale**
                                                   │    │    │ 1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList**)**
                                                   │    │    │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCode` (Result: **$BuyerCodeFilter**)**
                                                   │    │    │ 3. **List Operation: **Head** on **$undefined** (Result: **$BidRound**)**
                                                   │    │    │ 4. 🔀 **DECISION:** `if $BidRound!=empty then $BidRound/Submitted else false`
                                                   │    │    │    ➔ **If [false]:**
                                                   │    │    │       1. **Create **AuctionUI.ReminderNotificationHelper** (Result: **$NewReminderNotificationHelper**)
      - Set **Email** = `$IteratorAccountReminder/Name`
      - Set **FirstName** = `$IteratorAccountReminder/FirstName`**
                                                   │    │    │       2. **Add **$$NewReminderNotificationHelper** to/from list **$ReminderNotificationHelperList****
                                                   │    │    │    ➔ **If [true]:**
                                                   │    │    └─ **End Loop**
                                                   │    └─ **End Loop**
                                                   └─ **End Loop**
                                                3. **List Operation: **Union** on **$undefined** (Result: **$DistinctEmails**)**
                                                4. 🔄 **LOOP:** For each **$IteratordDistinctEmails** in **$DistinctEmails**
                                                   │ 1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name=$IteratordDistinctEmails/Email]` (Result: **$EcoATMDirectUser**)**
                                                   │ 2. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailReminder_Generic****
                                                   └─ **End Loop**
                                                5. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                                6. 🏁 **END:** Return empty
                                             ➔ **If [true]:**
                                                1. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                                2. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **CreateList**
                                          2. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round=2]` (Result: **$BidRoundSelectionFilter_ReminderRound2**)**
                                          3. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction_ReminderRound2**)**
                                          4. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale' ) ] [AuctionUI.BidData_BuyerCode/AuctionUI.BidData[ (BidAmount > 0) and ( ( TargetPrice = 0 and BidAmount > 0) or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter_ReminderRound2/TargetPercent)) or (TargetPrice - BidAmount <= $BidRoundSelectionFilter_ReminderRound2/TargetValue) )]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle=$Auction_ReminderRound2/AuctionTitle]` (Result: **$BuyerCodeList_ReminderRound2**)**
                                          5. **Call Microflow **EcoATM_BuyerManagement.SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe** (Result: **$SpecialBuyerCodeList_ReminderRound2**)**
                                          6. **List Operation: **Union** on **$undefined** (Result: **$Round2ReminderActiveBuyerCodeList**)**
                                          7. **CreateList**
                                          8. 🔄 **LOOP:** For each **$IteratorBuyerCode_ReminderRound2** in **$Round2ReminderActiveBuyerCodeList**
                                             │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyerCode_ReminderRound2** (Result: **$Buyer_ReminderRound2**)**
                                             │ 2. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer_ReminderRound2** (Result: **$EcoATMDirectUserList_LoopReminderRound2**)**
                                             │ 3. **Add **$$EcoATMDirectUserList_LoopReminderRound2** to/from list **$EcoATMDirectUserList_ReminderRound2****
                                             └─ **End Loop**
                                          9. **List Operation: **Union** on **$undefined** (Result: **$EcoATMDirectUserList_ReminderRound2_Distinct**)**
                                          10. 🔄 **LOOP:** For each **$IteratorAccountReminder_1** in **$EcoATMDirectUserList_ReminderRound2_Distinct**
                                             │ 1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$IteratorAccountReminder_1** (Result: **$BuyerList_ReminderRound2**)**
                                             │ 2. 🔄 **LOOP:** For each **$IteratorBuyer_1** in **$BuyerList_ReminderRound2**
                                             │    │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer_1** (Result: **$BuyerCodeList_LoopReminderRound2**)**
                                             │    │ 2. 🔄 **LOOP:** For each **$IteratorBuyerCode_1** in **$BuyerCodeList_LoopReminderRound2**
                                             │    │    │ 1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList_ReminderRound2**)**
                                             │    │    │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCode_1` (Result: **$BuyerCodeFilter_ReminderRound2**)**
                                             │    │    │ 3. **List Operation: **Head** on **$undefined** (Result: **$BidRound_ReminderRound2**)**
                                             │    │    │ 4. 🔀 **DECISION:** `if $BidRound_ReminderRound2!=empty then $BidRound_ReminderRound2/Submitted else false`
                                             │    │    │    ➔ **If [false]:**
                                             │    │    │       1. **Create **AuctionUI.ReminderNotificationHelper** (Result: **$NewReminderNotificationHelper_ReminderRound2**)
      - Set **Email** = `$IteratorAccountReminder_1/Name`
      - Set **FirstName** = `$IteratorAccountReminder_1/FirstName`**
                                             │    │    │       2. **Add **$$NewReminderNotificationHelper_ReminderRound2** to/from list **$ReminderNotificationHelperList_Round2****
                                             │    │    │    ➔ **If [true]:**
                                             │    │    └─ **End Loop**
                                             │    └─ **End Loop**
                                             └─ **End Loop**
                                          11. **List Operation: **Union** on **$undefined** (Result: **$DistinctEmails_ReminderRound2**)**
                                          12. 🔄 **LOOP:** For each **$IteratordDistinctEmails_1** in **$DistinctEmails_ReminderRound2**
                                             │ 1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name=$IteratordDistinctEmails_1/Email]` (Result: **$EcoATMDirectUser_1**)**
                                             │ 2. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailReminder_Round_2****
                                             └─ **End Loop**
                                          13. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                          14. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Update **$SchedulingAuction** (and Save to DB)
      - Set **EmailReminders** = `AuctionUI.ENUM_ReminderEmails.OneHourSent`**
                              2. 🔀 **DECISION:** `$SchedulingAuction/Round=2`
                                 ➔ **If [false]:**
                                    1. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
                                       ➔ **If [false]:**
                                          1. **CreateList**
                                          2. 🔄 **LOOP:** For each **$IteratorAccountReminder** in **$BidderList**
                                             │ 1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$IteratorAccountReminder** (Result: **$BuyerList**)**
                                             │ 2. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
                                             │    │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
                                             │    │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe or $currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale` (Result: **$BuyerCodeList_DWandWholesale**)**
                                             │    │ 3. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList_DWandWholesale**
                                             │    │    │ 1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList**)**
                                             │    │    │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCode` (Result: **$BuyerCodeFilter**)**
                                             │    │    │ 3. **List Operation: **Head** on **$undefined** (Result: **$BidRound**)**
                                             │    │    │ 4. 🔀 **DECISION:** `if $BidRound!=empty then $BidRound/Submitted else false`
                                             │    │    │    ➔ **If [false]:**
                                             │    │    │       1. **Create **AuctionUI.ReminderNotificationHelper** (Result: **$NewReminderNotificationHelper**)
      - Set **Email** = `$IteratorAccountReminder/Name`
      - Set **FirstName** = `$IteratorAccountReminder/FirstName`**
                                             │    │    │       2. **Add **$$NewReminderNotificationHelper** to/from list **$ReminderNotificationHelperList****
                                             │    │    │    ➔ **If [true]:**
                                             │    │    └─ **End Loop**
                                             │    └─ **End Loop**
                                             └─ **End Loop**
                                          3. **List Operation: **Union** on **$undefined** (Result: **$DistinctEmails**)**
                                          4. 🔄 **LOOP:** For each **$IteratordDistinctEmails** in **$DistinctEmails**
                                             │ 1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name=$IteratordDistinctEmails/Email]` (Result: **$EcoATMDirectUser**)**
                                             │ 2. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailReminder_Generic****
                                             └─ **End Loop**
                                          5. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                          6. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                          2. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **CreateList**
                                    2. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round=2]` (Result: **$BidRoundSelectionFilter_ReminderRound2**)**
                                    3. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction_ReminderRound2**)**
                                    4. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale' ) ] [AuctionUI.BidData_BuyerCode/AuctionUI.BidData[ (BidAmount > 0) and ( ( TargetPrice = 0 and BidAmount > 0) or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter_ReminderRound2/TargetPercent)) or (TargetPrice - BidAmount <= $BidRoundSelectionFilter_ReminderRound2/TargetValue) )]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle=$Auction_ReminderRound2/AuctionTitle]` (Result: **$BuyerCodeList_ReminderRound2**)**
                                    5. **Call Microflow **EcoATM_BuyerManagement.SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe** (Result: **$SpecialBuyerCodeList_ReminderRound2**)**
                                    6. **List Operation: **Union** on **$undefined** (Result: **$Round2ReminderActiveBuyerCodeList**)**
                                    7. **CreateList**
                                    8. 🔄 **LOOP:** For each **$IteratorBuyerCode_ReminderRound2** in **$Round2ReminderActiveBuyerCodeList**
                                       │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyerCode_ReminderRound2** (Result: **$Buyer_ReminderRound2**)**
                                       │ 2. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer_ReminderRound2** (Result: **$EcoATMDirectUserList_LoopReminderRound2**)**
                                       │ 3. **Add **$$EcoATMDirectUserList_LoopReminderRound2** to/from list **$EcoATMDirectUserList_ReminderRound2****
                                       └─ **End Loop**
                                    9. **List Operation: **Union** on **$undefined** (Result: **$EcoATMDirectUserList_ReminderRound2_Distinct**)**
                                    10. 🔄 **LOOP:** For each **$IteratorAccountReminder_1** in **$EcoATMDirectUserList_ReminderRound2_Distinct**
                                       │ 1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$IteratorAccountReminder_1** (Result: **$BuyerList_ReminderRound2**)**
                                       │ 2. 🔄 **LOOP:** For each **$IteratorBuyer_1** in **$BuyerList_ReminderRound2**
                                       │    │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer_1** (Result: **$BuyerCodeList_LoopReminderRound2**)**
                                       │    │ 2. 🔄 **LOOP:** For each **$IteratorBuyerCode_1** in **$BuyerCodeList_LoopReminderRound2**
                                       │    │    │ 1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList_ReminderRound2**)**
                                       │    │    │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCode_1` (Result: **$BuyerCodeFilter_ReminderRound2**)**
                                       │    │    │ 3. **List Operation: **Head** on **$undefined** (Result: **$BidRound_ReminderRound2**)**
                                       │    │    │ 4. 🔀 **DECISION:** `if $BidRound_ReminderRound2!=empty then $BidRound_ReminderRound2/Submitted else false`
                                       │    │    │    ➔ **If [false]:**
                                       │    │    │       1. **Create **AuctionUI.ReminderNotificationHelper** (Result: **$NewReminderNotificationHelper_ReminderRound2**)
      - Set **Email** = `$IteratorAccountReminder_1/Name`
      - Set **FirstName** = `$IteratorAccountReminder_1/FirstName`**
                                       │    │    │       2. **Add **$$NewReminderNotificationHelper_ReminderRound2** to/from list **$ReminderNotificationHelperList_Round2****
                                       │    │    │    ➔ **If [true]:**
                                       │    │    └─ **End Loop**
                                       │    └─ **End Loop**
                                       └─ **End Loop**
                                    11. **List Operation: **Union** on **$undefined** (Result: **$DistinctEmails_ReminderRound2**)**
                                    12. 🔄 **LOOP:** For each **$IteratordDistinctEmails_1** in **$DistinctEmails_ReminderRound2**
                                       │ 1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name=$IteratordDistinctEmails_1/Email]` (Result: **$EcoATMDirectUser_1**)**
                                       │ 2. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailReminder_Round_2****
                                       └─ **End Loop**
                                    13. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                    14. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$SchedulingAuction** (and Save to DB)
      - Set **EmailReminders** = `AuctionUI.ENUM_ReminderEmails.FourHourSent`**
                        2. 🔀 **DECISION:** `$SchedulingAuction/Round=2`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
                                 ➔ **If [false]:**
                                    1. **CreateList**
                                    2. 🔄 **LOOP:** For each **$IteratorAccountReminder** in **$BidderList**
                                       │ 1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$IteratorAccountReminder** (Result: **$BuyerList**)**
                                       │ 2. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
                                       │    │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
                                       │    │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe or $currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale` (Result: **$BuyerCodeList_DWandWholesale**)**
                                       │    │ 3. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList_DWandWholesale**
                                       │    │    │ 1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList**)**
                                       │    │    │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCode` (Result: **$BuyerCodeFilter**)**
                                       │    │    │ 3. **List Operation: **Head** on **$undefined** (Result: **$BidRound**)**
                                       │    │    │ 4. 🔀 **DECISION:** `if $BidRound!=empty then $BidRound/Submitted else false`
                                       │    │    │    ➔ **If [false]:**
                                       │    │    │       1. **Create **AuctionUI.ReminderNotificationHelper** (Result: **$NewReminderNotificationHelper**)
      - Set **Email** = `$IteratorAccountReminder/Name`
      - Set **FirstName** = `$IteratorAccountReminder/FirstName`**
                                       │    │    │       2. **Add **$$NewReminderNotificationHelper** to/from list **$ReminderNotificationHelperList****
                                       │    │    │    ➔ **If [true]:**
                                       │    │    └─ **End Loop**
                                       │    └─ **End Loop**
                                       └─ **End Loop**
                                    3. **List Operation: **Union** on **$undefined** (Result: **$DistinctEmails**)**
                                    4. 🔄 **LOOP:** For each **$IteratordDistinctEmails** in **$DistinctEmails**
                                       │ 1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name=$IteratordDistinctEmails/Email]` (Result: **$EcoATMDirectUser**)**
                                       │ 2. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailReminder_Generic****
                                       └─ **End Loop**
                                    5. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                    6. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                                    2. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **CreateList**
                              2. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[Round=2]` (Result: **$BidRoundSelectionFilter_ReminderRound2**)**
                              3. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction_ReminderRound2**)**
                              4. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale' ) ] [AuctionUI.BidData_BuyerCode/AuctionUI.BidData[ (BidAmount > 0) and ( ( TargetPrice = 0 and BidAmount > 0) or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter_ReminderRound2/TargetPercent)) or (TargetPrice - BidAmount <= $BidRoundSelectionFilter_ReminderRound2/TargetValue) )]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle=$Auction_ReminderRound2/AuctionTitle]` (Result: **$BuyerCodeList_ReminderRound2**)**
                              5. **Call Microflow **EcoATM_BuyerManagement.SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe** (Result: **$SpecialBuyerCodeList_ReminderRound2**)**
                              6. **List Operation: **Union** on **$undefined** (Result: **$Round2ReminderActiveBuyerCodeList**)**
                              7. **CreateList**
                              8. 🔄 **LOOP:** For each **$IteratorBuyerCode_ReminderRound2** in **$Round2ReminderActiveBuyerCodeList**
                                 │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyerCode_ReminderRound2** (Result: **$Buyer_ReminderRound2**)**
                                 │ 2. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$Buyer_ReminderRound2** (Result: **$EcoATMDirectUserList_LoopReminderRound2**)**
                                 │ 3. **Add **$$EcoATMDirectUserList_LoopReminderRound2** to/from list **$EcoATMDirectUserList_ReminderRound2****
                                 └─ **End Loop**
                              9. **List Operation: **Union** on **$undefined** (Result: **$EcoATMDirectUserList_ReminderRound2_Distinct**)**
                              10. 🔄 **LOOP:** For each **$IteratorAccountReminder_1** in **$EcoATMDirectUserList_ReminderRound2_Distinct**
                                 │ 1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$IteratorAccountReminder_1** (Result: **$BuyerList_ReminderRound2**)**
                                 │ 2. 🔄 **LOOP:** For each **$IteratorBuyer_1** in **$BuyerList_ReminderRound2**
                                 │    │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer_1** (Result: **$BuyerCodeList_LoopReminderRound2**)**
                                 │    │ 2. 🔄 **LOOP:** For each **$IteratorBuyerCode_1** in **$BuyerCodeList_LoopReminderRound2**
                                 │    │    │ 1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList_ReminderRound2**)**
                                 │    │    │ 2. **List Operation: **Filter** on **$undefined** where `$IteratorBuyerCode_1` (Result: **$BuyerCodeFilter_ReminderRound2**)**
                                 │    │    │ 3. **List Operation: **Head** on **$undefined** (Result: **$BidRound_ReminderRound2**)**
                                 │    │    │ 4. 🔀 **DECISION:** `if $BidRound_ReminderRound2!=empty then $BidRound_ReminderRound2/Submitted else false`
                                 │    │    │    ➔ **If [false]:**
                                 │    │    │       1. **Create **AuctionUI.ReminderNotificationHelper** (Result: **$NewReminderNotificationHelper_ReminderRound2**)
      - Set **Email** = `$IteratorAccountReminder_1/Name`
      - Set **FirstName** = `$IteratorAccountReminder_1/FirstName`**
                                 │    │    │       2. **Add **$$NewReminderNotificationHelper_ReminderRound2** to/from list **$ReminderNotificationHelperList_Round2****
                                 │    │    │    ➔ **If [true]:**
                                 │    │    └─ **End Loop**
                                 │    └─ **End Loop**
                                 └─ **End Loop**
                              11. **List Operation: **Union** on **$undefined** (Result: **$DistinctEmails_ReminderRound2**)**
                              12. 🔄 **LOOP:** For each **$IteratordDistinctEmails_1** in **$DistinctEmails_ReminderRound2**
                                 │ 1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name=$IteratordDistinctEmails_1/Email]` (Result: **$EcoATMDirectUser_1**)**
                                 │ 2. **Call Microflow **AuctionUI.SUB_InventoryNotificationEmailReminder_Round_2****
                                 └─ **End Loop**
                              13. **Update **$SchedulingAuction** (and Save to DB)
      - Set **isReminderNotificationSent** = `true`**
                              14. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.