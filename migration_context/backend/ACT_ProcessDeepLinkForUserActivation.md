# Microflow Detailed Specification: ACT_ProcessDeepLinkForUserActivation

### 📥 Inputs (Parameters)
- **$GUID** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **ForgotPassword.ForgotPassword**  (Result: **$ForgotPasswordList**)**
3. **CreateList**
4. 🔄 **LOOP:** For each **$IteratorForgotPassword** in **$ForgotPasswordList**
   │ 1. **Call Microflow **Encryption.Decrypt** (Result: **$DecryptedGUID**)**
   │ 2. 🔀 **DECISION:** `$DecryptedGUID=$GUID`
   │    ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. **Add **$$IteratorForgotPassword** to/from list **$NewForgotPasswordList****
   └─ **End Loop**
5. 🔀 **DECISION:** `$NewForgotPasswordList!= empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$ForgotPassword**)**
      2. 🔀 **DECISION:** `[%CurrentDateTime%] > $ForgotPassword/ValidUntill`
         ➔ **If [true]:**
            1. **LogMessage**
            2. **Maps to Page: **AuctionUI.Step3_LinkExpired****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$ForgotPassword/IsSignUp`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$ForgotPassword/ForgotPassword.ForgotPassword_Account != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `contains(toLowerCase($ForgotPassword/EmailAddress),'@ecoatm.com')`
                           ➔ **If [false]:**
                              1. **Create **EcoATM_UserManagement.ActivateUser** (Result: **$NewAccountPasswordData**)
      - Set **email** = `$ForgotPassword/EmailAddress`
      - Set **UUID** = `$GUID`
      - Set **ActivateUser_ForgotPassword** = `$ForgotPassword`**
                              2. **Maps to Page: **EcoATM_UserManagement.Login_ActivateUser****
                              3. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.ACT_Process_EcoAtm_User_Activation****
                              2. **LogMessage**
                              3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **LogMessage**
                        2. **Delete**
                        3. **Maps to Page: **AuctionUI.Step3_LinkExpired****
                        4. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Call Microflow **ForgotPassword.CreateNewUserFromSignup****
                  2. 🔀 **DECISION:** `$ForgotPassword/ForgotPassword.ForgotPassword_Account != empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `contains(toLowerCase($ForgotPassword/EmailAddress),'@ecoatm.com')`
                           ➔ **If [false]:**
                              1. **Create **EcoATM_UserManagement.ActivateUser** (Result: **$NewAccountPasswordData**)
      - Set **email** = `$ForgotPassword/EmailAddress`
      - Set **UUID** = `$GUID`
      - Set **ActivateUser_ForgotPassword** = `$ForgotPassword`**
                              2. **Maps to Page: **EcoATM_UserManagement.Login_ActivateUser****
                              3. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.ACT_Process_EcoAtm_User_Activation****
                              2. **LogMessage**
                              3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **LogMessage**
                        2. **Delete**
                        3. **Maps to Page: **AuctionUI.Step3_LinkExpired****
                        4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **AuctionUI.Step3_LinkExpired****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.