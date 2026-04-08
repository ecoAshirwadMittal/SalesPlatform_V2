# Microflow Detailed Specification: Sub_ResumableUpload

### 📥 Inputs (Parameters)
- **$UploadURL** (Type: Variable)
- **$FileDocument** (Type: System.FileDocument)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Offset** = `0`**
2. **Call Microflow **Encryption.Decrypt** (Result: **$DecryptedToken**)**
3. **JavaCallAction**
4. **RestCall**
5. **Delete**
6. **Update Variable **$Offset** = `$Offset + (@Sharepoint.UploadChunkSize * 327680)`**
7. 🔀 **DECISION:** `$Offset > $FileDocument/Size or $latestHttpResponse/StatusCode = 201`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$CompletedFileResponse`
   ➔ **If [false]:**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Object] value.