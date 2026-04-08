# Page: InvalidRMA_Message_View_OLD

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

  ↳ [Click] → **Cancel Changes**
- 🧩 **Image** [Class: `ram-icon-color` | DP: {Spacing right: Outer small}] (ID: `com.mendix.widget.web.image.Image`)
    - datasource: icon
    - onClickType: action
    - widthUnit: auto
    - width: 100
    - heightUnit: auto
    - height: 100
    - iconSize: 25
    - displayAs: fullImage
- 🧩 **Accordion** [Class: `rma-file-upload-error` | DP: {Borders: Horizontal, Spacing top: Outer large}] (ID: `com.mendix.widget.web.accordion.Accordion`)
    ➤ **groups**
        - headerRenderMode: custom
        - headerText: Header
        - headerHeading: headingThree
        ➤ **headerContent** (Widgets)
        ➤ **content** (Widgets)
            ↳ [acti] → **Nanoflow**: `EcoATM_RMA.ACT_CopyText`
        - visible: `$InvalidRMAItems/InvalidIMEICount > 0`
        - loadContent: always
        - initialCollapsedState: collapsed
        - initiallyCollapsed: `true`
        - headerRenderMode: custom
        - headerText: Header
        - headerHeading: headingThree
        ➤ **headerContent** (Widgets)
        ➤ **content** (Widgets)
            ↳ [acti] → **Nanoflow**: `EcoATM_RMA.ACT_CopyText`
        - visible: `$InvalidRMAItems/DuplicateIMEICount > 0`
        - loadContent: always
        - initialCollapsedState: collapsed
        - initiallyCollapsed: `true`
        - headerRenderMode: custom
        - headerText: Header
        - headerHeading: headingThree
        ➤ **headerContent** (Widgets)
        ➤ **content** (Widgets)
            ↳ [acti] → **Nanoflow**: `EcoATM_RMA.ACT_CopyText`
        - visible: `$InvalidRMAItems/InvalidReasonCount > 0`
        - loadContent: always
        - initialCollapsedState: collapsed
        - initiallyCollapsed: `true`
    - expandBehavior: singleExpanded
    - showIcon: right
  ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RequestRMA`
