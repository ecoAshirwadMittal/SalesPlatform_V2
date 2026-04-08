# Page: CertificateManagement

**Allowed Roles:** Encryption.User

**Layout:** `Encryption.ResponsiveLayout_Certificate`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: Encryption.PGPCertificate.Reference]
        - header: Reference
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: Encryption.PGPCertificate.EmailAddress]
        - header: Email
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: Encryption.PGPCertificate.CertificateType]
        - header: Certificate Type
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: System.FileDocument.Name]
        - header: Name
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: none
    - onClickTrigger: single
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Microflow**: `Encryption.MB_GenerateKeyShowPage`
        ↳ [acti] → **Microflow**: `Encryption.MB_NewPubKey`
        ↳ [acti] → **Microflow**: `Encryption.MB_NewPrivateKey`
        ↳ [acti] → **Microflow**: `Encryption.MB_OpenCertificateDetails`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - itemSelectionMode: clear
    - loadingType: spinner
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
