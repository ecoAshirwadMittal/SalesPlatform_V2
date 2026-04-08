# Microflow Analysis: ACT_SubmitRMAFile_ConvertedToNanoflow

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)
- **$RMAFile** (A record of type: EcoATM_RMA.RMAFile)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Import Excel Data
      - Store the result in a new variable called **$RMARequest_ImportHelperList****
5. **Decision:** "Devices Exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Delete**
7. **Delete**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMAFile.InvalidReason] to: "'Invalid file. Zero IMEIs or serial numbers found.'"
      - **Save:** This change will be saved to the database immediately.**
9. **Create Object
      - Store the result in a new variable called **$NewUserMessage_1_1****
10. **Show Page**
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
