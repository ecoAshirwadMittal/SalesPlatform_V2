# Microflow Analysis: IVK_SortingSave

### Requirements (Inputs):
- **$MxSorting** (A record of type: XLSReport.MxSorting)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxXPath****
2. **Decision:** "Found"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Run another process: "XLSReport.ACr_Sorting"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
