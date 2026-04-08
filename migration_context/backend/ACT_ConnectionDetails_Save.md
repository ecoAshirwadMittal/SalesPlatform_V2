# Microflow Detailed Specification: ACT_ConnectionDetails_Save

### 📥 Inputs (Parameters)
- **$ConnectionDetails** (Type: SnowflakeRESTSQL.ConnectionDetails)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **SnowflakeRESTSQL.PrivateKey_RetrieveFromConnectionDetails** (Result: **$PrivateKey**)**
2. **Call Microflow **SnowflakeRESTSQL.ConnectionDetails_Validate** (Result: **$IsValid**)**
3. 🔀 **DECISION:** `$IsValid`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$ConnectionDetails/AuthenticationType`
         ➔ **If [KEYPAIR_JWT]:**
            1. **Call Microflow **SnowflakeRESTSQL.PrivateKey_EncryptAndSetPassphrase****
            2. **Call Microflow **SnowflakeRESTSQL.PrivateKey_Commit****
            3. **Call Microflow **SnowflakeRESTSQL.ConnectionDetails_Commit****
            4. **Close current page/popup**
            5. 🏁 **END:** Return empty
         ➔ **If [(empty)]:**
            1. 🏁 **END:** Return empty
         ➔ **If [OAUTH]:**
            1. **Call Microflow **SnowflakeRESTSQL.ConnectionDetails_Commit****
            2. **Close current page/popup**
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.