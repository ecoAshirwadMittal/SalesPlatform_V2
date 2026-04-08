# Microflow Detailed Specification: ACT_DownloadReserveBids

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_EB.ReserveBid**  (Result: **$ReserveBidList**)**
3. **Create **EcoATM_EB.ReserveBidFile** (Result: **$NewReserveBidFile**)
      - Set **ReserveBidFile_ReserveBid** = `$ReserveBidList`
      - Set **DeleteAfterDownload** = `true`**
4. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='EBPrice']` (Result: **$MxTemplate**)**
5. **JavaCallAction**
6. **Update **$NewReserveBidFile**
      - Set **Name** = `'EB_Pricing_'+ formatDateTime([%BeginOfCurrentDay%],'yyyyMMdd')+'.xlsx'`**
7. **DownloadFile**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.