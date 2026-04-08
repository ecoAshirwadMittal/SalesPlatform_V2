# Microflow Analysis: ACT_Offer_DownloadAJSONFile

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "EcoATM_PWS.SUB_Offer_ConvertToJSON"
      - Store the result in a new variable called **$JSONContent****
3. **Create Object
      - Store the result in a new variable called **$NewFileUploadProcessHelper****
4. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
5. **Download File**
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
