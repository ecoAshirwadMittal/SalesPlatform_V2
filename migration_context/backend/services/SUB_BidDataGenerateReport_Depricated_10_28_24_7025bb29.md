# Microflow Analysis: SUB_BidDataGenerateReport_Depricated_10_28_24

### Requirements (Inputs):
- **$Template** (A record of type: XLSReport.MxTemplate)
- **$BidDataDoc** (A record of type: AuctionUI.BidDataDoc)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$NewCustomExcel****
2. **Java Action Call
      - Store the result in a new variable called **$Document****
3. **Update the **$undefined** (Object):
      - Change [System.FileDocument.Name] to: "$BidDataDoc/Name"
      - **Save:** This change will be saved to the database immediately.**
4. **Download File**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
