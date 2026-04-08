# Microflow Detailed Specification: ACT_GetSharepointBidRoundURL

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$SharepointBidRoundBaseURL** = `@EcoATM_Direct_Sharepoint.SharepointBidRoundURL`**
2. **Create Variable **$rootfolder** = `@EcoATM_Direct_Sharepoint.SharepointRoundBidsRootFolderName`**
3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
4. **Create Variable **$FolderDatePart** = `formatDateTime($Week/WeekStartDateTime,'YYYY-MM-dd')`**
5. **Create Variable **$AuctionFolder** = `'auction_' + $FolderDatePart`**
6. **Create Variable **$URL** = `$SharepointBidRoundBaseURL + '/' + $rootfolder + '/' + $AuctionFolder + '/stage'`**
7. 🏁 **END:** Return `$URL`

**Final Result:** This process concludes by returning a [String] value.