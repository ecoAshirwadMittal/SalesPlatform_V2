# Microflow Analysis: SUB_QuerySnowflakeOnDemand

### Requirements (Inputs):
- **$IteratorWeek** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Execute Database Query
      - Store the result in a new variable called **$VW_SALE_ORDER_PO**** ⚠️ *(This step has a safety catch if it fails)*
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
