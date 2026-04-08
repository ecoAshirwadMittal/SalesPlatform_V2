# Microflow Detailed Specification: VAL_Offer_hasRespectedAvailableQuantities

### 📥 Inputs (Parameters)
- **$OfferItemList** (Type: EcoATM_PWS.OfferItem)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Create Variable **$IsRespectingAvailableQuantity** = `true`**
3. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Retrieve related **OfferItem_Device** via Association from **$IteratorOfferItem** (Result: **$Device**)**
   │ 2. 🔀 **DECISION:** `$Device!=empty`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `if $Device/ItemType='SPB' then $IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty!=empty else $Device/ATPQty!=empty`
   │          ➔ **If [true]:**
   │             1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus`
   │                ➔ **If [Accept]:**
   │                   1. 🔀 **DECISION:** `if $Device/ItemType='SPB' then (($IteratorOfferItem/OfferQuantity<=$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty) or $IteratorOfferItem/Reserved=true) else (($IteratorOfferItem/OfferQuantity<=$Device/ATPQty) or $IteratorOfferItem/Reserved=true)`
   │                      ➔ **If [true]:**
   │                      ➔ **If [false]:**
   │                         1. **Update Variable **$IsRespectingAvailableQuantity** = `false`**
   │                ➔ **If [Finalize]:**
   │                   1. 🔀 **DECISION:** `if $Device/ItemType='SPB' then ($IteratorOfferItem/CounterQuantity<=$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty or $IteratorOfferItem/Reserved) else ($IteratorOfferItem/CounterQuantity<=$Device/ATPQty or $IteratorOfferItem/Reserved)`
   │                      ➔ **If [true]:**
   │                      ➔ **If [false]:**
   │                         1. **Update Variable **$IsRespectingAvailableQuantity** = `false`**
   │                ➔ **If [(empty)]:**
   │                   1. **Update Variable **$IsRespectingAvailableQuantity** = `false`**
   │                ➔ **If [Decline]:**
   │                ➔ **If [Counter]:**
   │                   1. 🔀 **DECISION:** `if $Device/ItemType='SPB' then ($IteratorOfferItem/CounterQuantity<=$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty or $IteratorOfferItem/Reserved) else ($IteratorOfferItem/CounterQuantity<=$Device/ATPQty or $IteratorOfferItem/Reserved)`
   │                      ➔ **If [true]:**
   │                      ➔ **If [false]:**
   │                         1. **Update Variable **$IsRespectingAvailableQuantity** = `false`**
   │          ➔ **If [false]:**
   │             1. **Update Variable **$IsRespectingAvailableQuantity** = `false`**
   │    ➔ **If [false]:**
   │       1. **Update Variable **$IsRespectingAvailableQuantity** = `false`**
   └─ **End Loop**
4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
5. 🏁 **END:** Return `$IsRespectingAvailableQuantity`

**Final Result:** This process concludes by returning a [Boolean] value.