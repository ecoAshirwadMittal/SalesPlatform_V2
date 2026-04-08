# Page: BidDownloadOnBuyerCodeSelect

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesRep

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
    - itemSelectionMode: clear
    - onClickTrigger: single
- 📦 **DataView** [Context]
  - 🧩 **HTMLSnippet** (ID: `HTMLSnippet.widget.HTMLSnippet`)
      - contenttype: html
      - contents: <span class='confirmationheader confirmationheadercolor'>Bidding </span> <span class='confirmationheader'>has ended.</span>
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round1`
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round1`
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round2`
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round1`
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round2`
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round3`
