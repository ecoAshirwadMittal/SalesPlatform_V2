# Microflow Detailed Specification: DS_GetOrCreateOrderItem_CaseLot

### 📥 Inputs (Parameters)
- **$Device** (Type: EcoATM_PWSMDM.Device)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$CaseLot** (Type: EcoATM_PWSMDM.CaseLot)
- **$ENUM_BuyerOfferItemType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.BuyerOfferItem** Filter: `[ ( EcoATM_PWS.BuyerOfferItem_Device = $Device and EcoATM_PWS.BuyerOfferItem_BuyerOffer = $BuyerOffer and EcoATM_PWS.BuyerOfferItem_BuyerCode= $BuyerCode and EcoATM_PWS.BuyerOfferItem_CaseLot=$CaseLot ) ]` (Result: **$BuyerOfferItem**)**
2. 🔀 **DECISION:** `$BuyerOfferItem != empty`
   ➔ **If [true]:**
      1. **Update **$BuyerOfferItem** (and Save to DB)
      - Set **OfferPrice** = `if $BuyerOfferItem/OfferPrice = empty or $BuyerOfferItem/OfferPrice = 0 or $BuyerOfferItem/Quantity = empty or $BuyerOfferItem/Quantity = 0 then $Device/CurrentListPrice else $BuyerOfferItem/OfferPrice`
      - Set **TotalPrice** = `if $BuyerOfferItem/OfferPrice = empty or $BuyerOfferItem/OfferPrice = 0 or $BuyerOfferItem/Quantity = empty or $BuyerOfferItem/Quantity = 0 then empty else $BuyerOfferItem/OfferPrice * $CaseLot/CaseLotSize`
      - Set **CaseOfferTotalPrice** = `if $BuyerOfferItem/OfferPrice = empty or $BuyerOfferItem/OfferPrice = 0 or $BuyerOfferItem/Quantity = empty or $BuyerOfferItem/Quantity = 0 then empty else $BuyerOfferItem/OfferPrice * $CaseLot/CaseLotSize* $BuyerOfferItem/Quantity`
      - Set **IsExceedQty** = `$Device != empty and $CaseLot/CaseLotATPQty!= empty and $BuyerOfferItem/Quantity != empty and $BuyerOfferItem/Quantity > $CaseLot/CaseLotATPQty and $CaseLot/CaseLotATPQty<= 100`
      - Set **_Type** = `$ENUM_BuyerOfferItemType`**
      2. **Call Microflow **EcoATM_PWS.CAL_BuyerOfferItem_CSSStyle** (Result: **$CSSClass**)**
      3. **Update **$BuyerOfferItem**
      - Set **CSSClass** = `''`**
      4. 🏁 **END:** Return `$BuyerOfferItem`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.BuyerOfferItem** (Result: **$NewOrderItem**)
      - Set **OfferPrice** = `$Device/CurrentListPrice`
      - Set **Quantity** = `empty`
      - Set **TotalPrice** = `empty`
      - Set **CaseOfferTotalPrice** = `empty`
      - Set **IsExceedQty** = `false`
      - Set **_Type** = `$ENUM_BuyerOfferItemType`
      - Set **BuyerOfferItem_Device** = `$Device`
      - Set **BuyerOfferItem_BuyerOffer** = `$BuyerOffer`
      - Set **BuyerOfferItem_BuyerCode** = `$BuyerCode`
      - Set **BuyerOfferItem_CaseLot** = `$CaseLot`**
      2. **Call Microflow **EcoATM_PWS.CAL_BuyerOfferItem_CSSStyle** (Result: **$NewCSSClass**)**
      3. **Update **$NewOrderItem**
      - Set **CSSClass** = `$NewCSSClass`**
      4. 🏁 **END:** Return `$NewOrderItem`

**Final Result:** This process concludes by returning a [Object] value.