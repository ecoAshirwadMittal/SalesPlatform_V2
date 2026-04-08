# Page: PG_BidCarryover_Confirmation

**Allowed Roles:** EcoATM_BidData.User

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 🧩 **HTML Element** [Style: `color:#3C3C3C;font-size:32px;font-weight:500;`] (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
    - tagName: div
    - tagNameCustom: div
    - tagContentMode: innerHTML
    - tagContentHTML: Do you want to <span style='color:#00969f'>Carryover Bids</span>?
- 🧩 **HTML Element** [Style: `font-size:16px;color:#666766;`] (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
    - tagName: div
    - tagNameCustom: div
    - tagContentMode: innerHTML
    - tagContentHTML: <ol type="1"> <li>This will carry over your bids and qty caps from your last submission.</li> <li>Any bids you’ve already entered in this round will be replaced with your carryover bids.</li> <li>You will still need to review and update your bids / qty caps based on this week’s target prices and available qty.</li> </ol>
  ↳ [acti] → **Nanoflow**: `EcoATM_BidData.NF_Start_CarryOverBids_JA`
  ↳ [acti] → **Close Page**
