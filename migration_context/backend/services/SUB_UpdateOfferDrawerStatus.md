# Microflow Detailed Specification: SUB_UpdateOfferDrawerStatus

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **CreateList**
3. **CreateList**
4. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
5. 🔀 **DECISION:** `$Offer/OfferStatus`
   ➔ **If [Sales_Review]:**
      1. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
         │ 1. **Update **$IteratorOfferItem**
      - Set **OfferDrawerStatus** = `EcoATM_PWS.ENUM_OfferDrawerStatus.Sales_Review`**
         │ 2. **Create Variable **$IsCaseLot** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot!=empty`**
         │ 3. 🔀 **DECISION:** `$IsCaseLot`
         │    ➔ **If [false]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForDevice** (Result: **$Device_SR**)**
         │       2. **Add **$$Device_SR** to/from list **$DeviceList****
         │    ➔ **If [true]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForCaseLot** (Result: **$CaseLot**)**
         │       2. **Add **$$CaseLot
** to/from list **$CaseLotList****
         └─ **End Loop**
      2. **Commit/Save **$OfferItemList** to Database**
      3. **Commit/Save **$DeviceList** to Database**
      4. **Commit/Save **$CaseLotList** to Database**
      5. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$Account**)**
      6. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      7. **Call Microflow **EcoATM_PWS.SUB_SetAndResetTags_PerOffer****
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      9. 🏁 **END:** Return empty
   ➔ **If [Buyer_Acceptance]:**
      1. 🔄 **LOOP:** For each **$IteratorOfferItem_1** in **$OfferItemList**
         │ 1. **Update **$IteratorOfferItem_1**
      - Set **OfferDrawerStatus** = `if($IteratorOfferItem_1/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept) then EcoATM_PWS.ENUM_OfferDrawerStatus.Accepted else if($IteratorOfferItem_1/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Decline) then EcoATM_PWS.ENUM_OfferDrawerStatus.Seller_Declined else if($IteratorOfferItem_1/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Counter) then EcoATM_PWS.ENUM_OfferDrawerStatus.Countered else empty`**
         │ 2. **Create Variable **$IsCaseLot_1** = `$IteratorOfferItem_1/EcoATM_PWS.OfferItem_CaseLot!=empty`**
         │ 3. 🔀 **DECISION:** `$IsCaseLot_1`
         │    ➔ **If [false]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForDevice** (Result: **$Device_B**)**
         │       2. **Add **$$Device_B** to/from list **$DeviceList****
         │    ➔ **If [true]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForCaseLot** (Result: **$CaseLot_1**)**
         │       2. **Add **$$CaseLot_1
** to/from list **$CaseLotList****
         └─ **End Loop**
      2. **Commit/Save **$OfferItemList** to Database**
      3. **Commit/Save **$DeviceList** to Database**
      4. **Commit/Save **$CaseLotList** to Database**
      5. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$Account**)**
      6. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      7. **Call Microflow **EcoATM_PWS.SUB_SetAndResetTags_PerOffer****
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      9. 🏁 **END:** Return empty
   ➔ **If [Ordered]:**
      1. 🔄 **LOOP:** For each **$IteratorOfferItem_2** in **$OfferItemList**
         │ 1. **Update **$IteratorOfferItem_2**
      - Set **OfferDrawerStatus** = `if($IteratorOfferItem_2/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept) then EcoATM_PWS.ENUM_OfferDrawerStatus.Ordered else if($IteratorOfferItem_2/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Decline) then EcoATM_PWS.ENUM_OfferDrawerStatus.Seller_Declined else if($IteratorOfferItem_2/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Counter) then if($IteratorOfferItem_2/BuyerCounterStatus = EcoATM_PWS.ENUM_CounterStatus.Accept) then EcoATM_PWS.ENUM_OfferDrawerStatus.Ordered else if($IteratorOfferItem_2/BuyerCounterStatus = EcoATM_PWS.ENUM_CounterStatus.Decline) then EcoATM_PWS.ENUM_OfferDrawerStatus.Buyer_Declined else empty else empty`**
         │ 2. **Create Variable **$IsCaseLot_2** = `$IteratorOfferItem_2/EcoATM_PWS.OfferItem_CaseLot!=empty`**
         │ 3. 🔀 **DECISION:** `$IsCaseLot_2`
         │    ➔ **If [false]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForDevice** (Result: **$Device_O**)**
         │       2. **Add **$$Device_O** to/from list **$DeviceList****
         │    ➔ **If [true]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForCaseLot** (Result: **$CaseLot_2**)**
         │       2. **Add **$$CaseLot_2
** to/from list **$CaseLotList****
         └─ **End Loop**
      2. **Commit/Save **$OfferItemList** to Database**
      3. **Commit/Save **$DeviceList** to Database**
      4. **Commit/Save **$CaseLotList** to Database**
      5. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$Account**)**
      6. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      7. **Call Microflow **EcoATM_PWS.SUB_SetAndResetTags_PerOffer****
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      9. 🏁 **END:** Return empty
   ➔ **If [Declined]:**
      1. 🔄 **LOOP:** For each **$IteratorOfferItem_3** in **$OfferItemList**
         │ 1. **Update **$IteratorOfferItem_3**
      - Set **OfferDrawerStatus** = `if($Offer/SellerOfferCancelled) then EcoATM_PWS.ENUM_OfferDrawerStatus.Seller_Declined else if $Offer/BuyerOfferCancelled then EcoATM_PWS.ENUM_OfferDrawerStatus.Buyer_Declined else if($IteratorOfferItem_3/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Decline) then EcoATM_PWS.ENUM_OfferDrawerStatus.Seller_Declined else if($IteratorOfferItem_3/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Counter) then EcoATM_PWS.ENUM_OfferDrawerStatus.Buyer_Declined else empty`**
         │ 2. **Create Variable **$IsCaseLot_3** = `$IteratorOfferItem_3/EcoATM_PWS.OfferItem_CaseLot!=empty`**
         │ 3. 🔀 **DECISION:** `$IsCaseLot_3`
         │    ➔ **If [false]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForDevice** (Result: **$Device**)**
         │       2. **Add **$$Device** to/from list **$DeviceList****
         │    ➔ **If [true]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForCaseLot** (Result: **$CaseLot_3**)**
         │       2. **Add **$$CaseLot_3
** to/from list **$CaseLotList****
         └─ **End Loop**
      2. **Commit/Save **$OfferItemList** to Database**
      3. **Commit/Save **$DeviceList** to Database**
      4. **Commit/Save **$CaseLotList** to Database**
      5. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$Account**)**
      6. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      7. **Call Microflow **EcoATM_PWS.SUB_SetAndResetTags_PerOffer****
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      9. 🏁 **END:** Return empty
   ➔ **If [Pending_Order]:**
      1. 🔄 **LOOP:** For each **$IteratorOfferItem_2** in **$OfferItemList**
         │ 1. **Update **$IteratorOfferItem_2**
      - Set **OfferDrawerStatus** = `if($IteratorOfferItem_2/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept) then EcoATM_PWS.ENUM_OfferDrawerStatus.Ordered else if($IteratorOfferItem_2/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Decline) then EcoATM_PWS.ENUM_OfferDrawerStatus.Seller_Declined else if($IteratorOfferItem_2/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Counter) then if($IteratorOfferItem_2/BuyerCounterStatus = EcoATM_PWS.ENUM_CounterStatus.Accept) then EcoATM_PWS.ENUM_OfferDrawerStatus.Ordered else if($IteratorOfferItem_2/BuyerCounterStatus = EcoATM_PWS.ENUM_CounterStatus.Decline) then EcoATM_PWS.ENUM_OfferDrawerStatus.Buyer_Declined else empty else empty`**
         │ 2. **Create Variable **$IsCaseLot_2** = `$IteratorOfferItem_2/EcoATM_PWS.OfferItem_CaseLot!=empty`**
         │ 3. 🔀 **DECISION:** `$IsCaseLot_2`
         │    ➔ **If [false]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForDevice** (Result: **$Device_O**)**
         │       2. **Add **$$Device_O** to/from list **$DeviceList****
         │    ➔ **If [true]:**
         │       1. **Call Microflow **EcoATM_PWS.SUB_ReserveQuantityForCaseLot** (Result: **$CaseLot_2**)**
         │       2. **Add **$$CaseLot_2
** to/from list **$CaseLotList****
         └─ **End Loop**
      2. **Commit/Save **$OfferItemList** to Database**
      3. **Commit/Save **$DeviceList** to Database**
      4. **Commit/Save **$CaseLotList** to Database**
      5. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$Account**)**
      6. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      7. **Call Microflow **EcoATM_PWS.SUB_SetAndResetTags_PerOffer****
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      9. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.