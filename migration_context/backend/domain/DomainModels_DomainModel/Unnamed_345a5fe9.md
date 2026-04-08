# Domain Model

## Entities

### 📦 ForgotPassword
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `EmailAddress` | StringAttribute | 200 | - | - |
| `Username` | StringAttribute | 200 | - | - |
| `UserFullname` | StringAttribute | 200 | - | - |
| `ForgotPasswordGUID` | StringAttribute | 200 | - | - |
| `ForgotPasswordURL` | StringAttribute | 500 | - | - |
| `ValidUntill` | DateTimeAttribute | - | - | - |
| `IsSignUp` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Administrator | ❌ | ✅ | ✅ | ✅ | - |

### 📦 Configuration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `HasTemplate` | BooleanAttribute | - | - | - |
| `HasDeeplink` | BooleanAttribute | - | - | - |
| `HasSignUpTemplate` | BooleanAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 AccountPasswordData
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `NewPassword` | StringAttribute | 200 | - | - |
| `ConfirmPassword` | StringAttribute | 200 | - | - |
| `IsLengthValid` | Enum(`AuctionUI.ENUM_PasswordStatus`) | - | Neutral | - |
| `HasUppercaseLetter` | Enum(`AuctionUI.ENUM_PasswordStatus`) | - | Neutral | - |
| `HasSpecialCharacter` | Enum(`AuctionUI.ENUM_PasswordStatus`) | - | Neutral | - |
| `NewPasswordValidationMessage` | StringAttribute | 200 | - | - |
| `ConfirmPasswordValidationMessage` | StringAttribute | 200 | - | - |
| `isNewPasswordValid` | BooleanAttribute | - | true | - |
| `isConfirmPasswordValid` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Administrator, ForgotPassword.Guest_ResetPassword, ForgotPassword.Guest_SignUp | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SignInHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UUID` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Guest_ResetPassword, ForgotPassword.Guest_SignUp | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SignupHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UserFullname` | StringAttribute | 200 | - | - |
| `EmailAddress` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Guest_SignUp | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ForgotPasswordHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `EmailAddress` | StringAttribute | 200 | - | - |
| `emailValidationMessage` | StringAttribute | 200 | - | - |
| `isEmailValid` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Administrator, ForgotPassword.Guest_ResetPassword | ❌ | ✅ | ✅ | ❌ | - |

### 📦 EmailTemplateLanguage
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 EmailTemplateSMTP
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ForgotPassword.Administrator | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `AccountPasswordData_ForgotPassword` | 1d09bd43-20a9-47fb-96d3-b4f9a85ce15a | beccf7bd-2e88-4a7d-b82f-2eab5d4dda99 | Reference | Default | DeleteMeButKeepReferences |
