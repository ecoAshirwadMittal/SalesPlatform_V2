# Microflow Detailed Specification: SUB_LoadBuyerDetails

### 📥 Inputs (Parameters)
- **$CurrentBuyerSummary** (Type: EcoATM_DA.BuyerSummary)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_DA.BuyerDetail** Filter: `[ EcoATM_DA.BuyerDetail_BuyerSummary = $CurrentBuyerSummary ]` (Result: **$ExistingBuyerDetails**)**
2. **Delete**
3. **Create Variable **$BuyerDetails** = `$CurrentBuyerSummary/CurrentEcoATMGradeDetails`**
4. **ImportXml**
5. 🔄 **LOOP:** For each **$IteratorBuyerDetail** in **$BuyerDetails1**
   │ 1. **DB Retrieve **EcoATM_MDM.MasterDeviceInventory** Filter: `[ECOATM_CODE = $IteratorBuyerDetail/ProductId]` (Result: **$MasterDeviceInventory**)**
   │ 2. **Update **$IteratorBuyerDetail**
      - Set **Brand** = `$MasterDeviceInventory/DEVICE_BRAND`
      - Set **Model** = `$MasterDeviceInventory/DEVICE_MODEL`**
   └─ **End Loop**
6. **Call Microflow **EcoATM_Reports.SUB_CalculateBuyerDetailsTotal****
7. **LogMessage**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.