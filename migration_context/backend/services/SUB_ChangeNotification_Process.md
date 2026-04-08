# Microflow Detailed Specification: SUB_ChangeNotification_Process

### 📥 Inputs (Parameters)
- **$httpRequest** (Type: System.HttpRequest)

### ⚙️ Execution Flow (Logic Steps)
1. **ImportXml**
2. **LogMessage**
3. **Retrieve related **Response_ValidationTokens** via Association from **$Response** (Result: **$ValidationTokens**)**
4. 🔀 **DECISION:** `$ValidationTokens = empty`
   ➔ **If [true]:**
      1. **Retrieve related **Value_Response** via Association from **$Response** (Result: **$Value**)**
      2. **Retrieve related **ChangeNotification_Value** via Association from **$Value** (Result: **$ChangeNotificationList**)**
      3. 🔄 **LOOP:** For each **$IteratorChangeNotification** in **$ChangeNotificationList**
         │ 1. **DB Retrieve **MicrosoftGraph.Subscription** Filter: `[ClientState = $IteratorChangeNotification/ClientState] [_Id = $IteratorChangeNotification/SubscriptionId]` (Result: **$Subscription**)**
         │ 2. 🔀 **DECISION:** `$Subscription != empty`
         │    ➔ **If [true]:**
         │       1. **LogMessage**
         │       2. **Retrieve related **ResourceData_ChangeNotification** via Association from **$IteratorChangeNotification** (Result: **$ResourceData**)**
         │    ➔ **If [false]:**
         └─ **End Loop**
      4. 🔀 **DECISION:** `$ChangeNotificationList != empty`
         ➔ **If [true]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `empty`
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **Call Microflow **MicrosoftGraph.SUB_ValidationTokens_Process** (Result: **$Valid**)**
      2. 🔀 **DECISION:** `$Valid`
         ➔ **If [true]:**
            1. **Retrieve related **Value_Response** via Association from **$Response** (Result: **$Value**)**
            2. **Retrieve related **ChangeNotification_Value** via Association from **$Value** (Result: **$ChangeNotificationList**)**
            3. 🔄 **LOOP:** For each **$IteratorChangeNotification** in **$ChangeNotificationList**
               │ 1. **DB Retrieve **MicrosoftGraph.Subscription** Filter: `[ClientState = $IteratorChangeNotification/ClientState] [_Id = $IteratorChangeNotification/SubscriptionId]` (Result: **$Subscription**)**
               │ 2. 🔀 **DECISION:** `$Subscription != empty`
               │    ➔ **If [true]:**
               │       1. **LogMessage**
               │       2. **Retrieve related **ResourceData_ChangeNotification** via Association from **$IteratorChangeNotification** (Result: **$ResourceData**)**
               │    ➔ **If [false]:**
               └─ **End Loop**
            4. 🔀 **DECISION:** `$ChangeNotificationList != empty`
               ➔ **If [true]:**
                  1. **LogMessage**
                  2. 🏁 **END:** Return `empty`
               ➔ **If [false]:**
                  1. **LogMessage**
                  2. 🏁 **END:** Return `empty`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [String] value.