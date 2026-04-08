# Nanoflow: ACT_SubmitCart

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep, EcoATM_PWS.SalesLeader

## 📥 Inputs

- **$BuyerOffer** (EcoATM_PWS.BuyerOffer)

## ⚙️ Execution Flow

1. **DB Retrieve **EcoATM_PWS.BuyerOffer** Filter: `[id = $BuyerOffer]` (Result: **$BuyerOffer_Current**)**
2. 🔀 **DECISION:** `$BuyerOffer_Current/OfferQuantity = empty or $BuyerOffer_Current/OfferQuantity = 0 or $BuyerOffer_Current/OfferSKUs = empty or $BuyerOffer_Current/OfferSKUs = 0 or $BuyerOffer_Current/OfferTotal = empty or $BuyerOffer_Current/OfferTotal = 0`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_PWS.BuyerOfferItem** Filter: `[EcoATM_PWS.BuyerOfferItem_BuyerOffer=$BuyerOffer] [TotalPrice>0]` (Result: **$BuyerOfferItemWithPriceList**)**
      2. **Create Variable **$IsExceeding** = `false`**
      3. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem** in **$BuyerOfferItemWithPriceList**
         │ 1. 🔀 **DECISION:** `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB'`
         │    ➔ **If [true]:**
         │       1. **Retrieve related **BuyerOfferItem_CaseLot** via Association from **$IteratorBuyerOfferItem** (Result: **$CaseLot**)**
         │       2. 🔀 **DECISION:** `$IteratorBuyerOfferItem/Quantity > $CaseLot/CaseLotATPQty and $CaseLot/CaseLotATPQty<= 100`
         │          ➔ **If [false]:**
         │             1. ⏭️ **CONTINUE** (next iteration)
         │          ➔ **If [true]:**
         │             1. **Update Variable **$IsExceeding** = `true`**
         │             2. **Update **$IteratorBuyerOfferItem**
      - Set **IsExceedQty** = `true`**
         │    ➔ **If [false]:**
         │       1. **Retrieve related **BuyerOfferItem_Device** via Association from **$IteratorBuyerOfferItem** (Result: **$Device**)**
         │       2. 🔀 **DECISION:** `$IteratorBuyerOfferItem/Quantity > $Device/ATPQty and $Device/ATPQty <= 100`
         │          ➔ **If [false]:**
         │             1. ⏭️ **CONTINUE** (next iteration)
         │          ➔ **If [true]:**
         │             1. **Update Variable **$IsExceeding** = `true`**
         │             2. **Update **$IteratorBuyerOfferItem**
      - Set **IsExceedQty** = `true`**
         └─ **End Loop**
      4. 🔀 **DECISION:** `not($IsExceeding)`
         ➔ **If [true]:**
            1. **List Operation: **FilterByExpression** on **$BuyerOfferItemWithPriceList** where `$currentObject/OfferPrice < $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice` (Result: **$BuyerOfferItem_RedList**)**
            2. 🔀 **DECISION:** `length($BuyerOfferItem_RedList) <= 0`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.ACT_Offer_SubmitOrder****
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage**)
      - Set **Title** = `'Almost Done!'`
      - Set **Message** = `if length($BuyerOfferItem_RedList) = 1 then length($BuyerOfferItem_RedList) + ' SKU will need to be reviewed by our sales team.' else length($BuyerOfferItem_RedList) + ' SKUs will need to be reviewed by our sales team.'`
      - Set **CSSClass** = `'pws-background'`
      - Set **IsSuccess** = `true`**
                  2. **Open Page: **EcoATM_PWS.PWS_AlmostDone****
                  3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$BuyerOffer** (and Save to DB)
      - Set **IsExceedingQty** = `true`**
            2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_CartSubmitted**)
      - Set **Title** = `'Cart Submitted!'`
      - Set **Message** = `'The cart you are attemping to submit has already been submitted in another window.'`
      - Set **CSSClass** = `'pws-background'`
      - Set **IsSuccess** = `true`**
      2. **Open Page: **EcoATM_PWS.PWS_OfferAlreadySubmitted****
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
