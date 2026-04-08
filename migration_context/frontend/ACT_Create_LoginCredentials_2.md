# Nanoflow: ACT_Create_LoginCredentials_2

**Allowed Roles:** AuctionUI.Anonymous, AuctionUI.Administrator

## ⚙️ Execution Flow

1. **DB Retrieve **System.User** Filter: `[id='[%CurrentUser%]']` (Result: **$User**)**
2. 🔀 **DECISION:** `$User!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **Retrieve related **Session_User** via Association from **$User** (Result: **$SessionList**)**
      3. 🔀 **DECISION:** `$SessionList=empty`
         ➔ **If [true]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_UserManagement.LoginCredentials** (Result: **$NewLoginCredentials**)**
            3. **Call Nanoflow **AuctionUI.ACT_GetLocalStoredLogin** (Result: **$LoginCredentials**)**
            4. 🏁 **END:** Return `$LoginCredentials`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Call Microflow **AuctionUI.SUB_ShowHomePage****
            3. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `empty`

## ⚠️ Error Handling

- On error in **DB Retrieve **System.User** Filter: `[id='[%CurrentUser%]']` (Result: **$User**)** → Call Microflow **Custom_Logging.SUB_Log_Info**

## 🏁 Returns
`Object`
