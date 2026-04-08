# Microflow Analysis: IVK_RecalculateSize

### Execution Steps:
1. **Search the Database for **MxModelReflection.DbSizeEstimate** using filter: { [NrOfRecords!=empty]
[MxModelReflection.DbSizeEstimate_MxObjectType/MxModelReflection.MxObjectType] } (Call this list **$DbSizeEstimateList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
