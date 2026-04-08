# Microflow Detailed Specification: ACT_UploadReserveBidFile

### 📥 Inputs (Parameters)
- **$ReserveBidFile** (Type: EcoATM_EB.ReserveBidFile)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_GET_CurrentUser** (Result: **$EcoATMDirectUser**)**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **ImportExcelData**
4. **DB Retrieve **EcoATM_EB.ReserveBid**  (Result: **$ReserveBidList**)**
5. **CreateList**
6. **CreateList**
7. 🔄 **LOOP:** For each **$IteratorEB_Pricing** in **$EB_PricingList**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/ProductId=$IteratorEB_Pricing/ProductID and $currentObject/Grade=$IteratorEB_Pricing/Grade` (Result: **$ReserveBid**)**
   │ 2. 🔀 **DECISION:** `$ReserveBid!=empty`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `round($IteratorEB_Pricing/Price,2)!=round($ReserveBid/Bid,2)`
   │          ➔ **If [true]:**
   │             1. **Create **EcoATM_EB.ReservedBidAudit** (Result: **$NewReservedBidAudit**)
      - Set **OldPrice** = `$ReserveBid/Bid`
      - Set **NewPrice** = `$IteratorEB_Pricing/Price`
      - Set **ReservedBidAudit_ReserveBid** = `$ReserveBid`**
   │             2. **Add **$$NewReservedBidAudit
** to/from list **$ReservedBidAuditList****
   │             3. **Update **$ReserveBid**
      - Set **Bid** = `$IteratorEB_Pricing/Price`
      - Set **LastUpdateDateTime** = `[%CurrentDateTime%]`**
   │             4. **Add **$$ReserveBid
** to/from list **$ReservedBidChanged****
   │          ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. **Create **EcoATM_EB.ReserveBid** (Result: **$NewReserveBid**)
      - Set **ProductId** = `round($IteratorEB_Pricing/ProductID)`
      - Set **Grade** = `$IteratorEB_Pricing/Grade`
      - Set **Model** = `$IteratorEB_Pricing/ModelName`
      - Set **Bid** = `$IteratorEB_Pricing/Price`
      - Set **LastUpdateDateTime** = `[%CurrentDateTime%]`**
   │       2. **Add **$$NewReserveBid
** to/from list **$ReservedBidChanged****
   └─ **End Loop**
8. **Commit/Save **$ReservedBidChanged** to Database**
9. **Commit/Save **$ReservedBidAuditList** to Database**
10. **Close current page/popup**
11. **Delete**
12. **ExportXml**
13. **Call Microflow **EcoATM_EB.SUB_ReserveBidData_UpdateSnowflake****
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.