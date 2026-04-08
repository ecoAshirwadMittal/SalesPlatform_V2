# Page: BidDownloadOnSubmit_2

**Allowed Roles:** ECOATM_Buyer.Administrator, ECOATM_Buyer.Bidder, ECOATM_Buyer.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🔤 **Text**: "Switch" [Style: `color:#14AC36 !important;`]
- 🔤 **Text**: "Buyer Code" [Style: `padding-left:3px;`]
- 🧩 **Gallery** [DP: {Borders: Both}] (ID: `com.mendix.widget.web.gallery.Gallery`)
    ➤ **content** (Widgets)
      - 🖼️ **Image**: Hotel
    - desktopItems: 1
    - tabletItems: 1
    - phoneItems: 1
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: below
    - showEmptyPlaceholder: none
- 🔤 **Text**: "Switch" [Style: `color:#14AC36 !important;`]
- 🔤 **Text**: "Buyer Code" [Style: `padding-left:3px;`]
- 🧩 **Gallery** [DP: {Borders: Both}] (ID: `com.mendix.widget.web.gallery.Gallery`)
    ➤ **content** (Widgets)
      - 🖼️ **Image**: Hotel
    - desktopItems: 1
    - tabletItems: 1
    - phoneItems: 1
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: below
    - showEmptyPlaceholder: none
- 📦 **DataView** [Context]
  - 🧩 **HTMLSnippet** (ID: `HTMLSnippet.widget.HTMLSnippet`)
      - contenttype: html
      - contents: <span class='confirmationheader'>Your bids have been </span> <span class='confirmationheader confirmationheadercolor'>submitted!</span>
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR1`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR2`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR1`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR2`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR3`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR1`
