# Microflow Analysis: ACT_SetSharepointDownloadStart

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Search the Database for **AuctionUI.AllBidDownload_ScreenHelper** using filter: { Show everything } (Call this list **$AllBidDownload_ScreenHelper**)**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R1Caption] to: "if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.R2Caption] to: "if $SchedulingAuction/Round=2 and $SchedulingAuction/HasRound then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption"
      - Change [AuctionUI.AllBidDownload_ScreenHelper.UpsellCaption] to: "if $SchedulingAuction/Round=3 and $SchedulingAuction/HasRound then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
