# Microflow Detailed Specification: VAL_EcoAtmUser

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IsValid** = `true`**
2. 🔀 **DECISION:** `$EcoATMDirectUser/FirstName != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$EcoATMDirectUser/LastName!= empty`
         ➔ **If [false]:**
            1. **Update Variable **$IsValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$EcoATMDirectUser/Email!= empty`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$EcoATMDirectUser/Email!= empty`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
   ➔ **If [false]:**
      1. **Update Variable **$IsValid** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `$EcoATMDirectUser/LastName!= empty`
         ➔ **If [false]:**
            1. **Update Variable **$IsValid** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$EcoATMDirectUser/Email!= empty`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$EcoATMDirectUser/Email!= empty`
               ➔ **If [false]:**
                  1. **Update Variable **$IsValid** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                     ➔ **If [false]:**
                        1. **Update Variable **$IsValid** = `false`**
                        2. **ValidationFeedback**
                        3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        4. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        5. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        6. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`
                     ➔ **If [true]:**
                        1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
                        2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
                        3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
                        4. 🔀 **DECISION:** `$HasBidderRole = true`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$EcoATMDirectUser/System.UserRoles != empty`
                                 ➔ **If [false]:**
                                    1. **Update Variable **$IsValid** = `false`**
                                    2. **ValidationFeedback**
                                    3. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer != empty`
                                       ➔ **If [false]:**
                                          1. **Update Variable **$IsValid** = `false`**
                                          2. **ValidationFeedback**
                                          3. 🏁 **END:** Return `$IsValid`
                                       ➔ **If [true]:**
                                          1. 🏁 **END:** Return `$IsValid`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$IsValid`

**Final Result:** This process concludes by returning a [Boolean] value.