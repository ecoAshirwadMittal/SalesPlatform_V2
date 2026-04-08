# Microflow Detailed Specification: DS_RetrieveOfferItems

### 📥 Inputs (Parameters)
- **$Device** (Type: EcoATM_PWSMDM.Device)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device=$Device]` (Result: **$OfferItemList**)**
2. 🏁 **END:** Return `$OfferItemList`

**Final Result:** This process concludes by returning a [List] value.