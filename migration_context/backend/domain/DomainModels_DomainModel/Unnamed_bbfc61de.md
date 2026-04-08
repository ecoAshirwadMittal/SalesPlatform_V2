# Domain Model

## Entities

### 📦 ExampleConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Title` | StringAttribute | 200 | - | - |
| `Username` | StringAttribute | 200 | - | - |
| `Password` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Encryption.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PasswordData
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `NewPassword` | StringAttribute | 200 | - | - |
| `ConfirmPassword` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Encryption.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PGPCertificate
> This entity is used to hold the public and secret key ring for PGP encryption and decryption

**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CertificateType` | Enum(`Encryption.CertificateType`) | - | - | - |
| `PassPhrase_Plain` | StringAttribute | 20 | - | - |
| `PassPhrase_Encrypted` | StringAttribute | 100 | - | - |
| `Reference` | StringAttribute | 100 | - | - |
| `EmailAddress` | StringAttribute | 50 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Encryption.User | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `PasswordData_ExampleConfiguration` | a1f51420-5f72-4bb4-87d5-d31da811aeae | 48f89fd9-51ec-4fd2-945e-c6a0a1f76967 | Reference | Default | DeleteMeButKeepReferences |
| `SecretKey_PublicKey` | 901658b1-d26a-470b-bfa7-3fecbf9992c4 | 901658b1-d26a-470b-bfa7-3fecbf9992c4 | Reference | Default | DeleteMeButKeepReferences |
