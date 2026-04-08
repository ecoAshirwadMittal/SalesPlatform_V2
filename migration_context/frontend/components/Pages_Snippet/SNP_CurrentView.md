# Snippet: SNP_CurrentView

## Widget Tree

- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_GetOrSetBuyerCode_SessionAndTabHelper]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_DisplayViewAs_Helper]
    - 📦 **DataView** [MF: EcoATM_PWS.DS_GetCurrentUserBuyerCodes] 👁️ (If Bool is true/false)
      - 📦 **DataView** [MF: AuctionUI.SUB_BuyerCodeSelectSearchHelper_Create]
        - 🧩 **Combo box** [Class: `pws-layout-dropdown`] (ID: `com.mendix.widget.web.combobox.Combobox`)
            - source: context
            - optionsSourceType: association
            - optionsSourceDatabaseCaptionType: attribute
            - optionsSourceAssociationCaptionType: attribute
            - optionsSourceAssociationCaptionAttribute: [Attr: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper.comboBoxSearchHelper]
            - emptyOptionText: {1} {2}
            - filterType: contains
            - optionsSourceAssociationCustomContentType: yes
            ➤ **optionsSourceAssociationCustomContent** (Widgets)
              - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
                  - tagName: div
                  - tagNameCustom: div
                  - tagContentMode: innerHTML
                  - tagContentHTML: <span class="buyercodeselectpws_code">{1}</span><span class="buyercodeselectpws_buyer">{2}</span>
                  ➤ **attributes**
                      - attributeName: class
                      - attributeValueType: expression
                      - attributeValueExpression: `if $currentObject/Code = $dataView1/Code
then 
'view-as-selected'
else
''`
              - 🧩 **Image** [Class: `view-as-selected`] 👁️ (If: `$currentObject/Code = $dataView1/Code`) (ID: `com.mendix.widget.web.image.Image`)
                  - datasource: icon
                  - onClickType: action
                  - widthUnit: auto
                  - width: 100
                  - heightUnit: auto
                  - height: 100
                  - iconSize: 14
                  - displayAs: fullImage
            - optionsSourceDatabaseCustomContentType: no
            - selectionMethod: checkbox
            - selectedItemsStyle: text
            - selectAllButtonCaption: Select all
            - ariaRequired: `false`
            - clearButtonAriaLabel: Clear selection
            - removeValueAriaLabel: Remove value
            - a11ySelectedValue: Selected value:
            - a11yOptionsAvailable: Number of options available:
            - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
            - staticDataSourceCustomContentType: no
            - readOnlyStyle: text
            - loadingType: skeleton
            - selectedItemsSorting: none
