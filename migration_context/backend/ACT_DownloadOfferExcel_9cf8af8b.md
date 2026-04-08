# Microflow Analysis: ACT_DownloadOfferExcel

### Requirements (Inputs):
- **$OfferMasterHelper** (A record of type: EcoATM_PWS.OfferMasterHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Run another process: "EcoATM_PWS.SUB_ListOffersForDownload"
      - Store the result in a new variable called **$OfferList****
5. **Run another process: "EcoATM_PWS.SUB_GetOfferExcelFileName"
      - Store the result in a new variable called **$FileName****
6. **Create Object
      - Store the result in a new variable called **$NewOfferDownload****
7. **Create List
      - Store the result in a new variable called **$OfferDataExportList****
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Permanently save **$undefined** to the database.**
10. **Search the Database for **XLSReport.MxTemplate** using filter: { [
  (
    Name = 'OfferDownload'
  )
] } (Call this list **$MxTemplate**)**
11. **Java Action Call
      - Store the result in a new variable called **$OfferExcelFile**** ⚠️ *(This step has a safety catch if it fails)*
12. **Update the **$undefined** (Object):
      - Change [System.FileDocument.Name] to: "$FileName"
      - **Save:** This change will be saved to the database immediately.**
13. **Download File**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
