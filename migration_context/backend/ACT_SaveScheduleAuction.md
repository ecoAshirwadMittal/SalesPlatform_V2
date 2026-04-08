# Microflow Detailed Specification: ACT_SaveScheduleAuction

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. **Retrieve related **SchedulingAuction_Helper_Auction** via Association from **$SchedulingAuction_Helper** (Result: **$Auction**)**
3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
4. **DB Retrieve **AuctionUI.Auction** Filter: `[AuctionUI.Auction_Week=$Week] [AuctionStatus='Scheduled' or AuctionStatus='Started']` (Result: **$AuctionExistsForWeek**)**
5. 🔀 **DECISION:** `$AuctionExistsForWeek != empty`
   ➔ **If [false]:**
      1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList_existing**)**
      2. 🔀 **DECISION:** `$SchedulingAuctionList_existing!=empty`
         ➔ **If [true]:**
            1. **Delete**
            2. **CreateList**
            3. **Create **AuctionUI.SchedulingAuction** (Result: **$SchedulingAuctionRound1**)
      - Set **Auction_Week_Year** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay`
      - Set **Round** = `1`
      - Set **Name** = `'Round 1'`
      - Set **SchedulingAuction_Auction** = `$Auction`
      - Set **RoundStatus** = `$SchedulingAuction_Helper/Round1_Status`
      - Set **Start_DateTime** = `$SchedulingAuction_Helper/Round1_Start_DateTime`
      - Set **End_DateTime** = `$SchedulingAuction_Helper/Round1_End_DateTime`**
            4. **Add **$$SchedulingAuctionRound1
** to/from list **$SchedulingAuctionList_New****
            5. **Create **AuctionUI.SchedulingAuction** (Result: **$SchedulingAuctionRound2**)
      - Set **Auction_Week_Year** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay`
      - Set **Round** = `2`
      - Set **Name** = `'Round 2'`
      - Set **SchedulingAuction_Auction** = `$Auction`
      - Set **RoundStatus** = `if $SchedulingAuction_Helper/Round2_isActive then $SchedulingAuction_Helper/isRound2Active else AuctionUI.enum_SchedulingAuctionStatus.Unscheduled`
      - Set **Start_DateTime** = `$SchedulingAuction_Helper/Round2_Start_DateTime`
      - Set **End_DateTime** = `$SchedulingAuction_Helper/Round2_End_DateTime`
      - Set **HasRound** = `$SchedulingAuction_Helper/Round2_isActive`**
            6. **Add **$$SchedulingAuctionRound2
** to/from list **$SchedulingAuctionList_New****
            7. **Create **AuctionUI.SchedulingAuction** (Result: **$SchedulingAuctionRound3**)
      - Set **Auction_Week_Year** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay`
      - Set **Round** = `3`
      - Set **Name** = `'Upsell Round'`
      - Set **SchedulingAuction_Auction** = `$Auction`
      - Set **RoundStatus** = `if $SchedulingAuction_Helper/isRound3Active then $SchedulingAuction_Helper/Round3_Status else AuctionUI.enum_SchedulingAuctionStatus.Unscheduled`
      - Set **Start_DateTime** = `$SchedulingAuction_Helper/Round3_Start_DateTime`
      - Set **End_DateTime** = `$SchedulingAuction_Helper/Round3_End_Datetime`
      - Set **HasRound** = `$SchedulingAuction_Helper/isRound3Active`**
            8. **Add **$$SchedulingAuctionRound3
** to/from list **$SchedulingAuctionList_New****
            9. **Commit/Save **$SchedulingAuctionList_New** to Database**
            10. **Update **$AggInventoryHelper**
      - Set **HasAuction** = `true`
      - Set **HasSchedule** = `true`**
            11. **Update **$Auction** (and Save to DB)
      - Set **AuctionStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`**
            12. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendAuctionDataToSnowflake`
               ➔ **If [true]:**
                  1. **Call Microflow **AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Close current page/popup**
                  2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **CreateList**
            2. **Create **AuctionUI.SchedulingAuction** (Result: **$SchedulingAuctionRound1**)
      - Set **Auction_Week_Year** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay`
      - Set **Round** = `1`
      - Set **Name** = `'Round 1'`
      - Set **SchedulingAuction_Auction** = `$Auction`
      - Set **RoundStatus** = `$SchedulingAuction_Helper/Round1_Status`
      - Set **Start_DateTime** = `$SchedulingAuction_Helper/Round1_Start_DateTime`
      - Set **End_DateTime** = `$SchedulingAuction_Helper/Round1_End_DateTime`**
            3. **Add **$$SchedulingAuctionRound1
** to/from list **$SchedulingAuctionList_New****
            4. **Create **AuctionUI.SchedulingAuction** (Result: **$SchedulingAuctionRound2**)
      - Set **Auction_Week_Year** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay`
      - Set **Round** = `2`
      - Set **Name** = `'Round 2'`
      - Set **SchedulingAuction_Auction** = `$Auction`
      - Set **RoundStatus** = `if $SchedulingAuction_Helper/Round2_isActive then $SchedulingAuction_Helper/isRound2Active else AuctionUI.enum_SchedulingAuctionStatus.Unscheduled`
      - Set **Start_DateTime** = `$SchedulingAuction_Helper/Round2_Start_DateTime`
      - Set **End_DateTime** = `$SchedulingAuction_Helper/Round2_End_DateTime`
      - Set **HasRound** = `$SchedulingAuction_Helper/Round2_isActive`**
            5. **Add **$$SchedulingAuctionRound2
** to/from list **$SchedulingAuctionList_New****
            6. **Create **AuctionUI.SchedulingAuction** (Result: **$SchedulingAuctionRound3**)
      - Set **Auction_Week_Year** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekDisplay`
      - Set **Round** = `3`
      - Set **Name** = `'Upsell Round'`
      - Set **SchedulingAuction_Auction** = `$Auction`
      - Set **RoundStatus** = `if $SchedulingAuction_Helper/isRound3Active then $SchedulingAuction_Helper/Round3_Status else AuctionUI.enum_SchedulingAuctionStatus.Unscheduled`
      - Set **Start_DateTime** = `$SchedulingAuction_Helper/Round3_Start_DateTime`
      - Set **End_DateTime** = `$SchedulingAuction_Helper/Round3_End_Datetime`
      - Set **HasRound** = `$SchedulingAuction_Helper/isRound3Active`**
            7. **Add **$$SchedulingAuctionRound3
** to/from list **$SchedulingAuctionList_New****
            8. **Commit/Save **$SchedulingAuctionList_New** to Database**
            9. **Update **$AggInventoryHelper**
      - Set **HasAuction** = `true`
      - Set **HasSchedule** = `true`**
            10. **Update **$Auction** (and Save to DB)
      - Set **AuctionStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Scheduled`**
            11. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendAuctionDataToSnowflake`
               ➔ **If [true]:**
                  1. **Call Microflow **AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async****
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Close current page/popup**
                  2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Show Message (Warning): `{1}`**
      2. **Call Microflow **AuctionUI.ACT_OpenInventoryOverviewForSelectedAuction****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.