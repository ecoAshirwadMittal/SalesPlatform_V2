# Page: Buyer_Edit_Old

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🖼️ **Image**: Hotel
  - 🔤 **Text**: "Added" [Class: `control-label-standalone`]
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ToggleBuyerStatus`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ToggleBuyerStatus`
  - 📝 **DropDown**: dropDown1
  - 🔤 **Text**: "Data Wipe Code" [Class: `control-label-standalone`]
  - 📦 **DataView** [MF: AuctionUI.ACT_CreateBuyerCodeHelper_DataWipe]
      ↳ [acti] → **Microflow**: `AuctionUI.ACT_CreateBuyerCode`
    - 🧩 **Gallery** (ID: `com.mendix.widget.web.gallery.Gallery`)
        ➤ **content** (Widgets)
            ↳ [acti] → **Delete**
        - desktopItems: 3
        - tabletItems: 6
        - phoneItems: 3
        - pageSize: 20
        - pagination: virtualScrolling
        - pagingPosition: below
        - showEmptyPlaceholder: custom
        - itemSelectionMode: clear
        - onClickTrigger: single
  - 🔤 **Text**: "Purchasing Order Code" [Class: `control-label-standalone`]
  - 📦 **DataView** [MF: AuctionUI.ACT_CreateBuyerCodeHelper_PurchasingOrder]
      ↳ [acti] → **Microflow**: `AuctionUI.ACT_CreateBuyerCode`
    - 🧩 **Gallery** (ID: `com.mendix.widget.web.gallery.Gallery`)
        ➤ **content** (Widgets)
            ↳ [acti] → **Delete**
        - desktopItems: 3
        - tabletItems: 6
        - phoneItems: 3
        - pageSize: 20
        - pagination: virtualScrolling
        - pagingPosition: below
        - showEmptyPlaceholder: custom
        - itemSelectionMode: clear
        - onClickTrigger: single
  - 🔤 **Text**: "Wholesale Code" [Class: `control-label-standalone`]
  - 📦 **DataView** [MF: AuctionUI.ACT_CreateBuyerCodeHelper_Wholesale]
      ↳ [acti] → **Microflow**: `AuctionUI.ACT_CreateBuyerCode`
    - 🧩 **Gallery** (ID: `com.mendix.widget.web.gallery.Gallery`)
        ➤ **content** (Widgets)
            ↳ [acti] → **Delete**
        - desktopItems: 3
        - tabletItems: 6
        - phoneItems: 3
        - pageSize: 20
        - pagination: virtualScrolling
        - pagingPosition: below
        - showEmptyPlaceholder: custom
        - itemSelectionMode: clear
        - onClickTrigger: single
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `AuctionUI.DTA_Save_Buyer`
