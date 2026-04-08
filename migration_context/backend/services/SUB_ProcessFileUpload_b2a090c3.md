# Microflow Analysis: SUB_ProcessFileUpload

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$RMAFile** (A record of type: EcoATM_RMA.RMAFile)

### Execution Steps:
1. **Import Excel Data
      - Store the result in a new variable called **$RMARequest_ImportHelperList****
2. **Decision:** "Devices Exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAFile.InvalidReason] to: "'Invalid file. Zero IMEIs or serial numbers found.'"
      - **Save:** This change will be saved to the database immediately.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
