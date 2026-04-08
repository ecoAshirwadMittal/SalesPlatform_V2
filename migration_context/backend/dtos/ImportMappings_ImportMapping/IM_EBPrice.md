# Import Mapping: IM_EBPrice

**JSON Structure:** `EcoATM_EB.JSON_EBPrice`

## Mapping Structure

- **JsonObject** (Object) → `EcoATM_EB.ReserveBid`
  - **Ecoatm_code** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.ProductId`
  - **Device_brand** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.Brand`
  - **Device_model** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.Model`
  - **MergedGrade** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.Grade`
  - **Latest_bid** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.Bid`
  - **Bid_week_ending_date** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.BidValidWeekDate`
  - **Min_bid** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.LastAwardedMinPrice`
  - **Min_price_week_date** (Value)
    - Attribute: `EcoATM_EB.ReserveBid.LastAwardedWeek`
