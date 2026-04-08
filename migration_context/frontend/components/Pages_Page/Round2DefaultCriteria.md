# Page: Round2DefaultCriteria

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_Auctions_ControlCenter`
- 📦 **DataView** [MF: AuctionUI.DS_BidRoundSelectionFilter_Round2]
  - 🧩 **Image** (ID: `com.mendix.widget.web.image.Image`)
      - datasource: icon
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 📂 **GroupBox**: "Regular Buyer Settings" [DP: {Spacing bottom: Outer medium}]
    - 📝 **DropDown**: dropDown2
      ↳ [Change] → **Microflow**: `AuctionUI.OCH_BuyerQualification_Round2`
    - 📝 **DropDown**: dropDown1 ✏️ (Editable if: `$currentObject/RegularBuyerQualification=EcoATM_BuyerManagement.Enum_RegularBuyerQualification.AllBidders
or
$currentObject/RegularBuyerQualification=EcoATM_BuyerManagement.Enum_RegularBuyerQualification.Only_Qualified`)
      ↳ [Change] → **Microflow**: `AuctionUI.OCH_BuyerQualification_Round2`
    - 🧩 **Image** (ID: `com.mendix.widget.web.image.Image`)
        - datasource: icon
        - onClickType: action
        - widthUnit: auto
        - width: 100
        - heightUnit: auto
        - height: 100
        - iconSize: 14
        - displayAs: fullImage
  - 📂 **GroupBox**: "Special Treatment Buyer Settings" [DP: {Spacing bottom: Outer medium}]
    - ⚡ **Button**: radioButtons2
  - 🧩 **Data grid 2** [Class: `inventory_grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: rowClick
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: AuctionUI.TargetPriceFactor.MinimumValue]
          ➤ **content** (Widgets)
          - header: Minimum value
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: right
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: AuctionUI.TargetPriceFactor.MaximumValue]
          ➤ **content** (Widgets)
          - header: Maximum value
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: right
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: AuctionUI.TargetPriceFactor.FactorType]
          ➤ **content** (Widgets)
            - 📝 **DropDown**: dropDown3
          - header: Type
          ➤ **filter** (Widgets)
            - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                - selectedItemsStyle: text
                - selectionMethod: checkbox
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - showContentAs: customContent
          - attribute: [Attr: AuctionUI.TargetPriceFactor.FactorAmount]
          ➤ **content** (Widgets)
          - header: Factor amount
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: right
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: AuctionUI.BidRoundSelectionFilter.Round]
          ➤ **content** (Widgets)
              ↳ [acti] → **Delete**
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
      - pageSize: 50
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: double
      - configurationStorageType: attribute
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_AddTargetPrice`
  - 🧩 **Image** (ID: `com.mendix.widget.web.image.Image`)
      - datasource: icon
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_SaveRound2Criteria`
    ↳ [acti] → **Cancel Changes**
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
