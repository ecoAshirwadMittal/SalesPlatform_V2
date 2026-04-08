# Microflow Detailed Specification: SE_Cleanup

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create Variable **$TimePeriod** = `addDays( [%BeginOfCurrentDay%], abs(@Email_Connector.EmailLogRetention)*-1 )`**
3. **DB Retrieve **Email_Connector.EmailConnectorLog** Filter: `[Created<$TimePeriod]` (Result: **$EmailLogList**)**
4. 🔀 **DECISION:** `$EmailLogList != empty`
   ➔ **If [true]:**
      1. **Delete**
         *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. **Create Variable **$TimePeriod2** = `addDays( [%BeginOfCurrentDay%], abs(@Email_Connector.EmailLogRetention)*-1 )`**
      2. **DB Retrieve **Email_Connector.EmailMessage** Filter: `[Status = 'SENT' and SentDate<$TimePeriod2]` (Result: **$EmailList**)**
      3. 🔀 **DECISION:** `$EmailList != empty`
         ➔ **If [true]:**
            1. **Delete**
               *(Merging with existing path logic)*
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.