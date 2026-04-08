# Microflow Analysis: ACT_DownloadReserveBids

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_EB.ReserveBid** using filter: { Show everything } (Call this list **$ReserveBidList**)**
3. **Create Object
      - Store the result in a new variable called **$NewReserveBidFile****
4. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='EBPrice']
 } (Call this list **$MxTemplate**)**
5. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
6. **Update the **$undefined** (Object):
      - Change [System.FileDocument.Name] to: "'EB_Pricing_'+ formatDateTime([%BeginOfCurrentDay%],'yyyyMMdd')+'.xlsx'
"**
7. **Download File**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
