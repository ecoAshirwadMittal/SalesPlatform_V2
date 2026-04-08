# Snippet: SNIP_Configuration_EmailProtocol_Auto

## Widget Tree

- 📝 **CheckBox**: checkBox1 [DP: {Spacing top: Outer small}]
  ↳ [Change] → **Microflow**: `Email_Connector.OCH_IncomingEmailConfiguration_Select`
- 📦 **ListView** [Association: undefined] [DP: {Style: Lined, Hover style: [object Object]}] 👁️ (If ReceiveEmails is true/false)
  ↳ [click] → **Microflow**: `Email_Connector.OEN_SetIncomingServer`
- 📝 **CheckBox**: checkBox2 [DP: {Spacing top: Outer small}]
  ↳ [Change] → **Microflow**: `Email_Connector.OCH_OutgoingEmailConfiguration_Select`
- 📦 **ListView** [Association: undefined] [DP: {Style: Lined, Hover style: [object Object]}] 👁️ (If SendEmails is true/false)
  ↳ [click] → **Microflow**: `Email_Connector.OEN_SetOutgoingServer`
