# Microflow Detailed Specification: SUB_CreateQualifiedBuyersEntity

### 📥 Inputs (Parameters)
- **$BuyerCodeList** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$isSpecialTreatmentBuyer** (Type: Variable)
- **$enum_BuyerCodeQualificationType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$SchedulingAuction** (and Save to DB)
      - Set **SchedulingAuction_QualifiedBuyers** = `$BuyerCodeList`**
2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale' ) and ( EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active' ) ]` (Result: **$BuyerCodeList_All_Active**)**
3. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$QualifiedBuyerCodesList_Existing**)**
4. **List Operation: **Subtract** on **$undefined** (Result: **$BuyerCodeList_NotQualified**)**
5. **CreateList**
6. **Call Microflow **AuctionUI.SUB_ClearQualifiedBuyerList****
7. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │ 1. **Create **EcoATM_BuyerManagement.QualifiedBuyerCodes** (Result: **$NewQualifiedBuyerCodes**)
      - Set **QualifiedBuyerCodes_SchedulingAuction** = `$SchedulingAuction`
      - Set **Qualificationtype** = `$enum_BuyerCodeQualificationType`
      - Set **QualifiedBuyerCodes_BuyerCode** = `$IteratorBuyerCode`
      - Set **Included** = `if $enum_BuyerCodeQualificationType = EcoATM_BuyerManagement.enum_BuyerCodeQualificationType.Qualified then true else false`
      - Set **isSpecialTreatment** = `$isSpecialTreatmentBuyer`**
   │ 2. **Add **$$NewQualifiedBuyerCodes** to/from list **$QualifiedBuyerCodesList****
   └─ **End Loop**
8. **Commit/Save **$QualifiedBuyerCodesList** to Database**
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.