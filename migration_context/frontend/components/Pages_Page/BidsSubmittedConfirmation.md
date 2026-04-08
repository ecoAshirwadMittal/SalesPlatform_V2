# Page: BidsSubmittedConfirmation

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

- 🧩 **HTMLSnippet** (ID: `HTMLSnippet.widget.HTMLSnippet`)
    - contenttype: html
    - contents: <span class='confirmationheader'>Submitting </span> <span class='confirmationheader confirmationheadercolor'>Bid</span> <span class='confirmationheader'>is Final</span>
- 🧩 **HTMLSnippet** (ID: `HTMLSnippet.widget.HTMLSnippet`)
    - contenttype: html
    - contents: <span class="confirmationSubHeader">Leave a note for the buyer informing them of the purpose of the bid on their behalf</span>
- 📦 **DataView** [Context]
  ↳ [acti] → **Microflow**: `AuctionUI.ACT_SubmitBidData`
  ↳ [acti] → **Close Page**
