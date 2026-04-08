# Snippet: Snip_AppRegistration

## Widget Tree

- 📦 **DataView** [MF: DocumentGeneration.DS_RegistrationWizard_FindOrCreate]
    ↳ [acti] → **OpenLink**
  - 📦 **DataView** [Context] 👁️ (If ServiceType is _Local/_Cloud/_Private/(empty))
      ↳ [acti] → **Microflow**: `DocumentGeneration.ACT_Registration_Revoke`
      ↳ [acti] → **Microflow**: `DocumentGeneration.ACT_Registration_Renew`
      ↳ [acti] → **OpenLink**
    - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
        - source: context
        - optionsSourceType: enumeration
        - attributeEnumeration: [Attr: DocumentGeneration.RegistrationWizard.DeploymentType]
        - optionsSourceDatabaseCaptionType: attribute
        - optionsSourceAssociationCaptionType: attribute
        - filterType: none
        - optionsSourceAssociationCustomContentType: no
        - optionsSourceDatabaseCustomContentType: no
        - staticDataSourceCustomContentType: no
        - selectionMethod: checkbox
        - selectedItemsStyle: text
        - selectAllButtonCaption: Select all
        - readOnlyStyle: text
        - ariaRequired: `false`
        - clearButtonAriaLabel: Clear selection
        - removeValueAriaLabel: Remove value
        - a11ySelectedValue: Selected value:
        - a11yOptionsAvailable: Number of options available:
        - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
        - loadingType: spinner
        - selectedItemsSorting: none
      ↳ [acti] → **OpenLink**
      ↳ [Change] → **Nanoflow**: `DocumentGeneration.OCh_RegistrationWizard_ApplicationUrl`
      ↳ [acti] → **OpenLink**
      ↳ [acti] → **OpenLink**
      ↳ [acti] → **Microflow**: `DocumentGeneration.ACT_Registration_Start`
