# Microflow Detailed Specification: SUB_GenerateCaselotLineItems

### 📥 Inputs (Parameters)
- **$IteratorOfferItem** (Type: EcoATM_PWS.OfferItem)
- **$Device** (Type: EcoATM_PWSMDM.Device)
- **$OrderRequest** (Type: EcoATM_PWSIntegration.OrderRequest)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_CaseLot** via Association from **$IteratorOfferItem** (Result: **$CaseLot**)**
2. **Create Variable **$OfferQuantity** = `floor($IteratorOfferItem/FinalOfferQuantity div $CaseLot/CaseLotSize)`**
3. 🔄 **LOOP:** For each **$undefined** in **$undefined**
   │ 1. **Create **EcoATM_PWSIntegration.OrderLineItem** (Result: **$CounteredOrderLineItem**)
      - Set **Item_number** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **Quantity** = `if($Device/ItemType='SPB') then toString($CaseLot/CaseLotSize) else toString($IteratorOfferItem/FinalOfferQuantity)`
      - Set **UnitSellingPrice** = `toString($IteratorOfferItem/FinalOfferPrice)`
      - Set **OrderLineItem_OrderRequest** = `$OrderRequest`**
   │ 2. **Update Variable **$OfferQuantity** = `$OfferQuantity-1`**
   └─ **End Loop**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.