# Microflow Analysis: SUB_AllBidDataDownload_BatchDelete

### Requirements (Inputs):
- **$AllBidsDoc** (A record of type: AuctionUI.AllBidsDoc)

### Execution Steps:
1. **Search the Database for **AuctionUI.AllBidDownload** using filter: { [AuctionUI.AllBidDownload_AllBidsDoc = $AllBidsDoc] } (Call this list **$AllBidDownloadList_All**)**
2. **Aggregate List
      - Store the result in a new variable called **$TotalItems****
3. **Create Variable**
4. **Create Variable**
5. **Create Variable**
6. **Java Action Call
      - Store the result in a new variable called **$Variable****
7. **Search the Database for **AuctionUI.AllBidDownload** using filter: { [AuctionUI.AllBidDownload_AllBidsDoc = $AllBidsDoc] } (Call this list **$AllBidDownloadList**)**
8. **Aggregate List
      - Store the result in a new variable called **$RetrievedCount****
9. **Delete**
10. **Change Variable**
11. **Java Action Call
      - Store the result in a new variable called **$Variable_2****
12. **Log Message**
13. **Decision:** "end of list?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
