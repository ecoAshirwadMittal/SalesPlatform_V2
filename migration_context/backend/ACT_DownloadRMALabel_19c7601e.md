# Microflow Analysis: ACT_DownloadRMALabel

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$Document**** ⚠️ *(This step has a safety catch if it fails)*
2. **Update the **$undefined** (Object):
      - Change [System.FileDocument.DeleteAfterDownload] to: "true"**
3. **Download File**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
