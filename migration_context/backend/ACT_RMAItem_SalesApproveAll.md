# Microflow Detailed Specification: ACT_RMAItem_SalesApproveAll

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'ApproveAll'`**
2. **Create Variable **$Description** = `'ApproveAll RMA [Number:'+$RMA/Number+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
5. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList**
   │ 1. **Update **$IteratorRMAItem**
      - Set **Status** = `EcoATM_RMA.ENUM_RMAItemStatus.Approve`**
   └─ **End Loop**
6. **Commit/Save **$RMAItemList** to Database**
7. **Call Microflow **EcoATM_RMA.SUB_RMA_SetAllRMAItemsValid****
8. **Call Microflow **EcoATM_RMA.SUB_CalculateApprovedRMAValues** (Result: **$Variable**)**
9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.