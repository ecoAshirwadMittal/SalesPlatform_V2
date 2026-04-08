# Microflow Analysis: ACT_ChangeBuyerCodeSavedBidsToPreviouslySubmitted

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$BidDataList_ToCommit****
2. **Search the Database for **AuctionUI.BidData** using filter: { [
  (
    AuctionUI.BidData_BidRound = $BidRound
    and SubmittedBidAmount != empty
    and SubmittedDateTime < changedDate
  )
]
 } (Call this list **$BidDataList**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
