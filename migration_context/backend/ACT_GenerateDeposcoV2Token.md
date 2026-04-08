# Microflow Detailed Specification: ACT_GenerateDeposcoV2Token

### 📥 Inputs (Parameters)
- **$GenerateNewToken** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$CurrentTime** = `addHours([%CurrentDateTime%],-1)`**
2. **DB Retrieve **EcoATM_PWSIntegration.AccessToken** Filter: `[createdDate>=$CurrentTime]` (Result: **$AccessToken**)**
3. 🔀 **DECISION:** `$GenerateNewToken = true or $AccessToken=empty`
   ➔ **If [true]:**
      1. **Delete**
      2. **JavaCallAction**
      3. **RestCall**
      4. **ImportXml**
      5. **Create **EcoATM_PWSIntegration.AccessToken** (Result: **$NewAccessToken**)
      - Set **Access_token** = `$AccessToken_1/Access_token`**
      6. 🏁 **END:** Return `$AccessToken_1/Access_token`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$AccessToken/Access_token`

**Final Result:** This process concludes by returning a [String] value.