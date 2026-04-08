# Page: PWSOrder

**Allowed Roles:** EcoATM_PWS.Bidder, EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS`

## Widget Tree

- 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreatePWSOrderMDMHelper]
  - 📦 **DataView** [Context]
    - 📦 **DataView** [MF: AuctionUI.SUB_BuyerCodeSelectSearchHelper_Create]
      - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
          - source: context
          - optionsSourceType: association
          - optionsSourceDatabaseCaptionType: attribute
          - optionsSourceAssociationCaptionType: attribute
          - optionsSourceAssociationCaptionAttribute: [Attr: EcoATM_Caching.NP_BuyerCodeSelect_Helper.comboBoxSearchHelper]
          - emptyOptionText: Choose Buyer
          - filterType: contains
          - optionsSourceAssociationCustomContentType: yes
          ➤ **optionsSourceAssociationCustomContent** (Widgets)
            - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
                - tagName: div
                - tagNameCustom: div
                - tagContentMode: innerHTML
                - tagContentHTML: <span class="buyercodeselect_code">{1}</span><span class="buyercodeselect_buyer">{2}</span>
          - optionsSourceDatabaseCustomContentType: no
          - selectionMethod: checkbox
          - selectedItemsStyle: text
          - selectAllButtonCaption: Select all
          - clearButtonAriaLabel: Clear selection
          - removeValueAriaLabel: Remove value
          - a11ySelectedValue: Selected value:
          - a11yOptionsAvailable: Number of options available:
          - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOffer_InProgress]
    - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-order-datagrid` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
            - header: SKU
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Model.Model]
            - header: Model
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 150
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Carrier.Carrier]
            - header: Carrier
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 70
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Capacity.Capacity]
            - header: Capacity
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 70
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Color.Color]
            - header: Color
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 70
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Condition.Condition]
            - header: Grade
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 70
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_PWS.OrderItem_NPE.CurrentListPrice]
            - dynamicText: {1}{2}
            - header: List Price
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 70
            - size: 1
            - alignment: center
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
            - dynamicText: {1}
            - header: Avl. Qty
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 70
            - size: 1
            - alignment: center
            - columnClass: `'pws-datagrid-background-color'
`
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.OrderItem_NPE.OfferPrice]
            ➤ **content** (Widgets)
                ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OrderItem_NPE`
            - header: Offer Price
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 75
            - size: 1
            - alignment: left
            - columnClass: `'user-data-cells'
`
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.OrderItem_NPE.Quantity]
            ➤ **content** (Widgets)
                ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OrderItem_NPE`
            - header: Qty
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 75
            - size: 1
            - alignment: center
            - columnClass: `'user-data-cells'
`
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.OrderItem_NPE.TotalPrice]
            ➤ **content** (Widgets)
            - header: Total
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 75
            - size: 1
            - alignment: left
            - columnClass: `'user-data-cells'
`
        - pageSize: 20
        - pagination: buttons
        - pagingPosition: bottom
        - showPagingButtons: always
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        - configurationStorageType: attribute
        - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
        ➤ **filterList**
            - filter: [Attr: EcoATM_PWSMDM.Device.DeviceDescription]
        ➤ **filtersPlaceholder** (Widgets)
          - 🖼️ **Image**: pws_search [DP: {Spacing left: Outer small}]
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
            ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OrderDataExport_ExportExcel`
          - 🔤 **Text**: "More Actions" [Class: `pws-usericon_settings_title`]
            ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_ResetOrder`
            ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_Listing_Download`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
