# Microflow Analysis: SUB_SendBuyerToSnowflake

### Requirements (Inputs):
- **$BuyerList** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
2. **Execute Database Query
      - Store the result in a new variable called **$DEV_UPSERT_BUYER_DATA**** ⚠️ *(This step has a safety catch if it fails)*
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
