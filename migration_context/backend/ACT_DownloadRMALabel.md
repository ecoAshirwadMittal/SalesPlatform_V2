# Microflow Detailed Specification: ACT_DownloadRMALabel

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Update **$Document**
      - Set **DeleteAfterDownload** = `true`**
3. **DownloadFile**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.