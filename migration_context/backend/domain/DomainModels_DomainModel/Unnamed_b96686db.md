# Domain Model

## Entities

### 📦 EcoATMDirectUser
> First Name Last Name Email Buyer Name (Company name) / “ecoATM” Last Login Date Role(s) Invited on (last time an invite was sent to this user)

**Extends:** `Administration.Account`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SubmissionID` | AutoNumberAttribute | - | 1 | - |
| `FirstName` | StringAttribute | 200 | - | - |
| `LastName` | StringAttribute | 200 | - | - |
| `InvitedDate` | DateTimeAttribute | - | - | - |
| `LastInviteSent` | DateTimeAttribute | - | - | - |
| `ActivationDate` | DateTimeAttribute | - | - | - |
| `Password_tmp` | StringAttribute | 200 | - | - |
| `PasswordConfirm_tmp` | StringAttribute | 200 | - | - |
| `IsBuyerRole` | BooleanAttribute | - | false | this field is used by the UX to control layout. |
| `UserBuyerDisplay` | StringAttribute | 400 | - | - |
| `UserRolesDisplay` | StringAttribute | 200 | - | - |
| `UserStatus` | Enum(`AuctionUI.enum_UserStatus`) | - | - | - |
| `Inactive` | BooleanAttribute | - | false | - |
| `OverallUserStatus` | Enum(`AuctionUI.enum_OverallUserStatus`) | - | - | - |
| `LandingPagePreference` | Enum(`EcoATM_UserManagement.ENUM_LandingPagePreference`) | - | - | - |
| `EntityOwner` | StringAttribute | 200 | - | - |
| `EntityChanger` | StringAttribute | 200 | - | - |
| `Acknowledgement` | BooleanAttribute | - | false | - |

#### Event Handlers

- **After Commit**: `AuctionUI.DAT_SetUserOverallStatus`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_UserManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_UserManagement.SalesLeader, EcoATM_UserManagement.SalesOps, EcoATM_UserManagement.SalesRep | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_UserManagement.User | ❌ | ✅ | ✅ | ❌ | - |
| EcoATM_UserManagement.Bidder | ❌ | ✅ | ✅ | ❌ | - |

### 📦 UserStatus
**Extends:** `System.Image`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UserStatus` | Enum(`AuctionUI.enum_UserStatus`) | - | - | - |
| `StatusText` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_UserManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ActivateUser
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `email` | StringAttribute | 200 | - | - |
| `Password` | StringAttribute | 200 | - | - |
| `ConfirmPassword` | StringAttribute | 200 | - | - |
| `UUID` | StringAttribute | 200 | - | - |
| `IsLengthValid` | Enum(`AuctionUI.ENUM_PasswordStatus`) | - | Neutral | - |
| `HasUpperCaseLetter` | Enum(`AuctionUI.ENUM_PasswordStatus`) | - | Neutral | - |
| `HasSpecialCharacter` | Enum(`AuctionUI.ENUM_PasswordStatus`) | - | Neutral | - |
| `NewPasswordValidationMessage` | StringAttribute | 200 | - | - |
| `ConfirmPasswordValidationMessage` | StringAttribute | 200 | - | - |
| `isNewPasswordValid` | BooleanAttribute | - | true | - |
| `isConfirmPasswordValid` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_UserManagement.Administrator, EcoATM_UserManagement.Anonymous, EcoATM_UserManagement.Bidder, EcoATM_UserManagement.ecoAtmDirectAdmin, EcoATM_UserManagement.Executive, EcoATM_UserManagement.SalesLeader, EcoATM_UserManagement.SalesOps, EcoATM_UserManagement.SalesRep, EcoATM_UserManagement.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 LoginCredentials
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Email` | StringAttribute | 200 | - | - |
| `Password` | StringAttribute | 200 | - | - |
| `ShowPasssword` | BooleanAttribute | - | true | - |
| `RememberMe` | BooleanAttribute | - | false | - |
| `LastLoginTime` | DateTimeAttribute | - | - | - |
| `isEmailValid` | BooleanAttribute | - | true | - |
| `emailValidationMessage` | StringAttribute | 200 | - | - |
| `isPasswordValid` | BooleanAttribute | - | true | - |
| `passwordValidationMessage` | StringAttribute | 200 | - | - |
| `emailInfoMessage` | StringAttribute | 200 | - | - |
| `showEmailInfo` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_UserManagement.Anonymous | ✅ | ✅ | ✅ | ❌ | - |
| EcoATM_UserManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_UserManagement.Bidder | ✅ | ✅ | ✅ | ❌ | - |

### 📦 NewUser_Helper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IsEcoAtmEmail` | BooleanAttribute | - | false | - |
| `email` | StringAttribute | 200 | - | - |
| `IsExternalUser` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_UserManagement.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 TestUserHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `From` | IntegerAttribute | - | - | - |
| `To` | IntegerAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_UserManagement.Administrator, EcoATM_UserManagement.ecoAtmDirectAdmin, EcoATM_UserManagement.Executive, EcoATM_UserManagement.SalesLeader, EcoATM_UserManagement.SalesOps, EcoATM_UserManagement.SalesRep | ❌ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `EcoATMDirectUser_UserStatus` | ec04fdc6-8792-4976-87cc-34b64fb7ddad | 5a4b7634-481a-46f0-b483-396b6264767a | Reference | Default | DeleteMeButKeepReferences |
| `Entity_EcoATMDirectUser` | f113eb69-3ffa-40e8-8ccf-9a2770b10c1e | ec04fdc6-8792-4976-87cc-34b64fb7ddad | Reference | Default | DeleteMeButKeepReferences |
