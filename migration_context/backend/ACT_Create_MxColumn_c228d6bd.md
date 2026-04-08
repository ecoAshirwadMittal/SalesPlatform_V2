# Microflow Analysis: ACT_Create_MxColumn

### Requirements (Inputs):
- **$MappingParent** (A record of type: XLSReport.MxSheet)
- **$Name** (A record of type: Object)
- **$Status** (A record of type: Object)
- **$ColumnNumber** (A record of type: Object)
- **$ObjectAttribute** (A record of type: Object)
- **$DataAggregate** (A record of type: Object)
- **$DataAggregateFunction** (A record of type: Object)
- **$ResultAggregate** (A record of type: Object)
- **$ResultAggregateFunction** (A record of type: Object)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxDataList****
2. **Decision:** "is List empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Create Object
      - Store the result in a new variable called **$NewMxColumn****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
