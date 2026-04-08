# Microflow Detailed Specification: VAL_BidDataTotalQuantityConfig

### 📥 Inputs (Parameters)
- **$BidDataTotalQuantityConfig_current** (Type: AuctionUI.BidDataTotalQuantityConfig)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty`
   ➔ **If [false]:**
      1. **Update Variable **$isValid** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/Grade!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/NonDWQuantity!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/NonDWQuantity!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/Grade!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/NonDWQuantity!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. **Update Variable **$isValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/NonDWQuantity!=empty`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/DataWipeQuantity!=empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$isValid** = `false`**
                        2. **ValidationFeedback**
                        3. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$BidDataTotalQuantityConfig_current/EcoID!=empty and $BidDataTotalQuantityConfig_current/Grade!=empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.VAL_TotalQuantityAlreadyExists** (Result: **$AlreadyExists**)**
                              2. 🏁 **END:** Return `$isValid and $AlreadyExists`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.