# Microflow Analysis: SUB_SyncRMAStatus

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$DesposcoAPI** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)
- **$RMAStatusList** (A record of type: EcoATM_RMA.RMAStatus)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "EcoATM_PWSIntegration.ACT_GenerateDeposcoV2Token"
      - Store the result in a new variable called **$AccessToken_2**** ⚠️ *(This step has a safety catch if it fails)*
5. **Change Variable**
6. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
7. **Run another process: "EcoATM_PWSIntegration.ACT_AuditRestAPICalls"**
8. **Decision:** "valid response?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **RetryNeeded?**
9. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
10. **Create Variable**
11. **Take the list **$RMAStatusList**, perform a [FindByExpression] where: { $currentObject/SystemStatus=$RMAResponse/OrderStatus }, and call the result **$RMAStatus****
12. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA_RMAStatus] to: "if $RMAStatus!=empty
then $RMAStatus
else $RMA/EcoATM_RMA.RMA_RMAStatus
"**
13. **Decision:** "Send to Snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
14. **Run another process: "EcoATM_RMA.SUB_SendOnlyRMADetailsToSnowflake"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
