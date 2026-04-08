# Microflow Analysis: SUB_TransferDADataToSharepoint

### Requirements (Inputs):
- **$DeviceBuyerList** (A record of type: EcoATM_DA.DeviceBuyer)
- **$BidDataList** (A record of type: AuctionUI.BidData)
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Week****
2. **Create List
      - Store the result in a new variable called **$BuyerCodeList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Create List
      - Store the result in a new variable called **$AllBidDownloadList****
5. **Create Object
      - Store the result in a new variable called **$EBBuyerCode****
6. **Change List**
7. **Run another process: "EcoATM_DA.SUB_CreateEBBidDataList"**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
