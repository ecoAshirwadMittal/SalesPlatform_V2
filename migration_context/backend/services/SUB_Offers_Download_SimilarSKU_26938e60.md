# Microflow Analysis: SUB_Offers_Download_SimilarSKU

### Requirements (Inputs):
- **$OfferDrawerHelper** (A record of type: EcoATM_PWS.OfferDrawerHelper)
- **$OfferItem** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Run another process: "EcoATM_PWS.DS_OfferDrawer_SimilarSKUs"
      - Store the result in a new variable called **$OfferItemList****
2. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'OfferDrawerSimilarSKU']
 } (Call this list **$MxTemplate**)**
3. **Create Object
      - Store the result in a new variable called **$OfferItemExcelDocument****
4. **Create List
      - Store the result in a new variable called **$OfferItemDataExportList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Java Action Call
      - Store the result in a new variable called **$Document**** ⚠️ *(This step has a safety catch if it fails)*
8. **Update the **$undefined** (Object):
      - Change [System.FileDocument.Name] to: "'SimilarSKUOffer.xlsx'
"
      - **Save:** This change will be saved to the database immediately.**
9. **Download File**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
