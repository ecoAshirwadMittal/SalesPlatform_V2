# Microflow Detailed Specification: SUB_RemoveBuyerOfferItem

### 📥 Inputs (Parameters)
- **$BuyerOfferItem** (Type: EcoATM_PWS.BuyerOfferItem)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$BuyerOfferItem**
      - Set **Quantity** = `0`**
2. **Call Microflow **EcoATM_PWS.OCH_OrderItem_Cart****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.