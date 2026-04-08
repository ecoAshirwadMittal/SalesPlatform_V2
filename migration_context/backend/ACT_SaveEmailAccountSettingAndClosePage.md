# Microflow Detailed Specification: ACT_SaveEmailAccountSettingAndClosePage

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailAccount/isIncomingEmailConfigured=true or $EmailAccount/isOutgoingEmailConfigured=true`
   ➔ **If [false]:**
      1. **Show Message (Information): `Both Send and Receive server details are not configured.`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **Email_Connector.SUB_EmailAccount_CheckServerConnection** (Result: **$isConnected**)**
      2. 🔀 **DECISION:** `$isConnected`
         ➔ **If [true]:**
            1. **Call Microflow **Email_Connector.SUB_EmailAccount_Save****
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.