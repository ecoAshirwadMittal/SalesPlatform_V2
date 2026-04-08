# Microflow Analysis: SUB_RetrieveShipmentDetails

### Requirements (Inputs):
- **$Auth** (A record of type: Object)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)
- **$ShipmentAPI** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$Order** (A record of type: EcoATM_PWS.Order)

### Execution Steps:
1. **Create Variable**
2. **Rest Call**
3. **Run another process: "EcoATM_PWSIntegration.ACT_AuditRestAPICalls"**
4. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
5. **Take the list **$ListOfShipments**, perform a [Head], and call the result **$HeadShipment****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
