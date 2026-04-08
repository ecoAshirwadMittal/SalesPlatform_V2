# Snippet: Deeplink_Details

## Widget Tree

  ↳ [acti] → **Microflow**: `DeepLink.ACT_DeleteDeepLink`
- 📑 **TabContainer**
  - 📑 **Tab**: "Configuration"
    - 📦 **DataView** [NF: DeepLink.GetURLByDeeplink]
        ↳ [acti] → **Nanoflow**: `DeepLink.OnClick_DeeplinkURL`
  - 📑 **Tab**: "Advanced"
    - 📝 **CheckBox**: checkBox1 [DP: {Spacing top: Outer small}]
    - 📝 **ReferenceSelector**: referenceSelector1 [DP: {Spacing top: Outer small}]
    - 📝 **CheckBox**: checkBox3 [DP: {Spacing top: Outer small}]
    - 📝 **CheckBox**: checkBox4 [DP: {Spacing top: Outer small}]
