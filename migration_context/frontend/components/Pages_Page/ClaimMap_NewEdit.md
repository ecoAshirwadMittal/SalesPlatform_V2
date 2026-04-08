# Page: ClaimMap_NewEdit

**Allowed Roles:** SAML20.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **ReferenceSelector**: referenceSelector1
  - ⚡ **Button**: Search [Style: Default]
    ↳ [acti] → **Microflow**: `SAML20.MB_ShowAttributesForConfig_ClaimMap`
  - 📝 **ReferenceSelector**: referenceSelector2
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
