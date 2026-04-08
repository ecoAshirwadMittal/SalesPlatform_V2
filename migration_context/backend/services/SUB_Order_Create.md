# Microflow Detailed Specification: SUB_Order_Create

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
2. **Create **EcoATM_PWS.Order** (Result: **$NewOrder**)
      - Set **Offer_Order** = `$Offer`
      - Set **Order_BuyerCode** = `$BuyerCode`**
3. 🏁 **END:** Return `$NewOrder`

**Final Result:** This process concludes by returning a [Object] value.