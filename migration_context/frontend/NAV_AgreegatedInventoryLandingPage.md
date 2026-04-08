# Nanoflow: NAV_AgreegatedInventoryLandingPage

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps, AuctionUI.SalesRep

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Call Microflow **AuctionUI.SUB_NavigateToAggregatedInventoryPage****
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
