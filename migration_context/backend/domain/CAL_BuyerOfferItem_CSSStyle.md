# Microflow Detailed Specification: CAL_BuyerOfferItem_CSSStyle

### 📥 Inputs (Parameters)
- **$BuyerOfferItem** (Type: EcoATM_PWS.BuyerOfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$BuyerOfferItem/OfferPrice!=empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BuyerOfferItem/Quantity!=empty and $BuyerOfferItem/Quantity>0`
         ➔ **If [true]:**
            1. **Retrieve related **BuyerOfferItem_Device** via Association from **$BuyerOfferItem** (Result: **$Device**)**
            2. 🔀 **DECISION:** `$Device/CurrentListPrice!=empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `($BuyerOfferItem/OfferPrice < $Device/CurrentListPrice)`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `($BuyerOfferItem/OfferPrice >= $Device/CurrentListPrice)`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `'default-data'`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `'offer-price-green'`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `'offer-price-orange'`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `'default-data'`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `'default-data'`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `'default-data'`

**Final Result:** This process concludes by returning a [String] value.