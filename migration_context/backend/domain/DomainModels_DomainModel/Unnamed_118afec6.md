# Domain Model

## Entities

### 📦 EmailAccount
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Username` | StringAttribute | 200 | - | - |
| `MailAddress` | StringAttribute | 200 | - | - |
| `Password` | StringAttribute | - | - | - |
| `Timeout` | IntegerAttribute | - | 20000 | - |
| `sanitizeEmailBodyForXSSScript` | BooleanAttribute | - | false | - |
| `isP12Configured` | BooleanAttribute | - | false | - |
| `isLDAPConfigured` | BooleanAttribute | - | false | - |
| `isIncomingEmailConfigured` | BooleanAttribute | - | false | - |
| `isOutgoingEmailConfigured` | BooleanAttribute | - | false | - |
| `FromDisplayName` | StringAttribute | 200 | - | - |
| `UseSSLCheckServerIdentity` | BooleanAttribute | - | false | - |
| `IsSharedMailbox` | BooleanAttribute | - | false | - |
| `isOAuthUsed` | BooleanAttribute | - | false | - |
| `isEmailConfigAutoDetect` | BooleanAttribute | - | true | - |
| `ComposeEmail` | BooleanAttribute | - | false | - |

#### Event Handlers

- **Before Commit**: `Email_Connector.BCO_EmailAccount_EncryptPassword`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 EmailMessage
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Subject` | StringAttribute | - | - | - |
| `SentDate` | DateTimeAttribute | - | - | - |
| `RetrieveDate` | DateTimeAttribute | - | [%CurrentDateTime%] | - |
| `From` | StringAttribute | - | - | - |
| `To` | StringAttribute | - | - | - |
| `CC` | StringAttribute | - | - | - |
| `BCC` | StringAttribute | - | - | - |
| `Content` | StringAttribute | - | - | - |
| `UseOnlyPlainText` | BooleanAttribute | - | false | - |
| `hasAttachments` | BooleanAttribute | - | false | - |
| `Size` | IntegerAttribute | - | 0 | - |
| `FromDisplayName` | StringAttribute | 200 | - | - |
| `ReplyTo` | StringAttribute | 200 | - | - |
| `PlainBody` | StringAttribute | - | - | - |
| `QueuedForSending` | BooleanAttribute | - | false | - |
| `ResendAttempts` | IntegerAttribute | - | 0 | - |
| `LastSendError` | StringAttribute | - | - | - |
| `LastSendAttemptAt` | DateTimeAttribute | - | - | - |
| `Status` | Enum(`Email_Connector.ENUM_EmailStatus`) | - | - | - |
| `isSigned` | BooleanAttribute | - | false | - |
| `isEncrypted` | BooleanAttribute | - | false | - |
| `RecipientsToggle` | BooleanAttribute | - | false | - |

#### Indexes

- **Index**: `d86cdfdb-58a0-474b-8c40-92a29d05f187` (Ascending), `f2def56c-c174-4346-be2c-40914020a439` (Ascending), `4f8ce85a-c912-468d-80da-0e92e16580d8` (Ascending)
- **Index**: `0029214e-837b-4545-87e3-6dcb20eec452` (Ascending)
- **Index**: `4f8ce85a-c912-468d-80da-0e92e16580d8` (Ascending), `d86cdfdb-58a0-474b-8c40-92a29d05f187` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Attachment
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ContentID` | StringAttribute | - | - | - |
| `attachmentName` | StringAttribute | - | - | The length of this attribute was set to 200, but a |
| `attachmentSize` | IntegerAttribute | - | 0 | - |
| `attachmentContentType` | StringAttribute | - | - | The length of this attribute was set to 200, but a |
| `Position` | Enum(`Email_Connector.ENUM_AttachmentPosition`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Folder
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 EmailTemplate
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TemplateName` | StringAttribute | 200 | - | - |
| `CreationDate` | DateTimeAttribute | - | [%CurrentDateTime%] | - |
| `Subject` | StringAttribute | 200 | - | - |
| `SentDate` | DateTimeAttribute | - | - | - |
| `To` | StringAttribute | - | - | - |
| `CC` | StringAttribute | - | - | - |
| `BCC` | StringAttribute | - | - | - |
| `Content` | StringAttribute | - | - | - |
| `UseOnlyPlainText` | BooleanAttribute | - | false | - |
| `hasAttachment` | BooleanAttribute | - | false | - |
| `ReplyTo` | StringAttribute | 200 | - | - |
| `PlainBody` | StringAttribute | - | - | - |
| `FromDisplayName` | StringAttribute | 200 | - | - |
| `Signed` | BooleanAttribute | - | false | - |
| `Encrypted` | BooleanAttribute | - | false | - |
| `RecipientsToggle` | BooleanAttribute | - | false | - |
| `FromAddress` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 EmailConnectorLog
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Created` | DateTimeAttribute | - | [%CurrentDateTime%] | - |
| `Logtype` | Enum(`Email_Connector.ENUM_LogType`) | - | - | - |
| `ErrorMessage` | StringAttribute | - | - | - |
| `TriggeredInMF` | StringAttribute | 200 | - | - |
| `StackTrace` | StringAttribute | - | - | - |
| `Message` | StringAttribute | 200 | - | - |
| `IsUnread` | BooleanAttribute | - | true | - |

#### Indexes

- **Index**: `17fbe599-b7bc-49e7-a5e4-8e0c5ff3f539` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ✅ | - |

### 📦 ModelReflectionChecker
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ModelReflectionSynced` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 IncomingEmailConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IncomingProtocol` | Enum(`Email_Connector.ENUM_IncomingProtocol`) | - | - | - |
| `Folder` | StringAttribute | 200 | INBOX | - |
| `UseBatchImport` | BooleanAttribute | - | false | - |
| `BatchSize` | IntegerAttribute | - | 50 | - |
| `Handling` | Enum(`Email_Connector.ENUM_MessageHandling`) | - | NoHandling | - |
| `MoveFolder` | StringAttribute | 200 | - | - |
| `ProcessInlineImage` | BooleanAttribute | - | false | - |
| `FetchStrategy` | Enum(`Email_Connector.ENUM_FetchStrategy`) | - | Latest | - |
| `NotifyOnNewEmails` | BooleanAttribute | - | false | - |
| `ServerHost` | StringAttribute | 200 | - | - |
| `ServerPort` | IntegerAttribute | - | 0 | - |

#### Event Handlers

- **After Commit**: `Email_Connector.ACO_ADE_IncomingAccountMetrics`
- **After Delete**: `Email_Connector.ACO_ADE_IncomingAccountMetrics`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 OutgoingEmailConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OutgoingProtocol` | Enum(`Email_Connector.ENUM_OutgoingProtocol`) | - | - | - |
| `SSL` | BooleanAttribute | - | false | - |
| `TLS` | BooleanAttribute | - | false | - |
| `SendMaxAttempts` | IntegerAttribute | - | 0 | - |
| `ServerHost` | StringAttribute | 200 | - | - |
| `ServerPort` | IntegerAttribute | - | 0 | - |

#### Event Handlers

- **After Commit**: `Email_Connector.ACO_ADE_OutgoingAccountMetrics`
- **After Delete**: `Email_Connector.ACO_ADE_OutgoingAccountMetrics`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 EmailProvider
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Username` | StringAttribute | 200 | - | - |
| `MailAddress` | StringAttribute | 200 | - | - |
| `Password` | StringAttribute | 200 | - | - |
| `ReceiveEmails` | BooleanAttribute | - | false | - |
| `SendEmails` | BooleanAttribute | - | false | - |
| `FromDisplayName` | StringAttribute | 200 | - | - |
| `isOAuthUsed` | BooleanAttribute | - | false | - |
| `IsSharedMailbox` | BooleanAttribute | - | false | - |
| `AuthType` | Enum(`Email_Connector.ENUM_OAuthType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 IncomingServer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `HostName` | StringAttribute | 200 | - | - |
| `Port` | IntegerAttribute | - | 0 | - |
| `SocketType` | StringAttribute | 200 | - | - |
| `IncomingProtocol` | Enum(`Email_Connector.ENUM_IncomingProtocol`) | - | - | - |
| `ReceiveIMAPS` | BooleanAttribute | - | true | - |
| `ReceivePOP3S` | BooleanAttribute | - | false | - |
| `SelectedIncomingServer` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OutgoingServer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `HostName` | StringAttribute | 200 | - | - |
| `Port` | IntegerAttribute | - | 0 | - |
| `OutgoingProtocol` | Enum(`Email_Connector.ENUM_OutgoingProtocol`) | - | - | - |
| `SSL` | BooleanAttribute | - | false | - |
| `TLS` | BooleanAttribute | - | false | - |
| `SendSMTP` | BooleanAttribute | - | false | - |
| `SelectedOutgoingServer` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SelectedConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IncomingHost` | StringAttribute | 200 | - | - |
| `IncomingPort` | IntegerAttribute | - | 0 | - |
| `IncomingSocketType` | StringAttribute | 200 | - | - |
| `IncomingProtocol` | Enum(`Email_Connector.ENUM_IncomingProtocol`) | - | - | - |
| `OutgoingHost` | StringAttribute | 200 | - | - |
| `OutgoingPort` | IntegerAttribute | - | 0 | - |
| `OutgoingProtocol` | Enum(`Email_Connector.ENUM_OutgoingProtocol`) | - | - | - |
| `OutgoingSSL` | BooleanAttribute | - | false | - |
| `OutgoingTLS` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Pk12Certificate
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Passphrase` | StringAttribute | 200 | - | - |

#### Event Handlers

- **Before Commit**: `Email_Connector.BCO_Pk12Certificate_EncryptPassphrase`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 LDAPConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LDAPHost` | StringAttribute | 200 | - | - |
| `LDAPPort` | IntegerAttribute | - | 389 | - |
| `LDAPUsername` | StringAttribute | 200 | - | - |
| `LDAPPassword` | StringAttribute | 200 | - | - |
| `isSSL` | BooleanAttribute | - | false | - |
| `BaseDN` | StringAttribute | 200 | - | - |
| `AuthType` | Enum(`Email_Connector.ENUM_LDAPAuthType`) | - | none | - |

#### Event Handlers

- **Before Commit**: `Email_Connector.BCO_LDAPConfiguration_EncryptPassword`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 LDAPBaseDN
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OAuthProvider
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 50 | - | - |
| `ClientID` | StringAttribute | - | - | - |
| `ClientSecret` | StringAttribute | - | - | - |
| `OpenIDWellKnownMetadataUri` | StringAttribute | 700 | - | - |
| `AuthorizationEndpoint` | StringAttribute | 700 | - | - |
| `TokenEndpoint` | StringAttribute | 700 | - | - |
| `EmailDomain` | StringAttribute | 200 | - | - |
| `CallbackOperationPath` | StringAttribute | 200 | - | - |
| `CallbackURL` | StringAttribute | 200 | - | - |
| `OAuthType` | Enum(`Email_Connector.ENUM_OAuthType`) | - | - | - |
| `TenantID` | StringAttribute | 200 | - | - |

#### Event Handlers

- **Before Commit**: `Email_Connector.BCO_OAuthProvider_EncryptPassword`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

#### Validation Rules

- **ClientID** (RequiredRuleInfo): "ClientID cannot be empty"
- **ClientSecret** (RequiredRuleInfo): "Client secret cannot be empty"
- **CallbackOperationPath** (RegExRuleInfo): "Callback operation path cannot end with /"
- **Name** (RequiredRuleInfo): "Name cannot be empty"

### 📦 OAuthToken
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `token_type` | StringAttribute | 200 | - | - |
| `scope` | StringAttribute | - | - | - |
| `expires_in` | IntegerAttribute | - | 0 | - |
| `access_token` | StringAttribute | - | - | - |
| `refresh_token` | StringAttribute | - | - | - |
| `id_token` | StringAttribute | - | - | - |

#### Event Handlers

- **Before Commit**: `Email_Connector.BCO_OAuthToken_EncryptTokens`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ✅ | - |

### 📦 ScopeSelected
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ScopeString` | StringAttribute | 1000 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Profile
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Email` | StringAttribute | - | - | - |
| `Name` | StringAttribute | - | - | - |
| `Username` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 QueryString
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Param` | StringAttribute | 1000 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OAuthNonce
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `State` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ✅ | - |

### 📦 EmailHeader
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Key` | StringAttribute | 200 | - | - |
| `Value` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Email_Connector.EmailConnectorAdmin | ❌ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `Attachment_EmailMessage` | f33d6e1a-94d4-47ba-8e24-f3ce19b0be51 | e1a94f92-a454-43a2-9aae-e4836ce49b54 | Reference | Default | DeleteMeButKeepReferences |
| `Attachment_EmailTemplate` | f33d6e1a-94d4-47ba-8e24-f3ce19b0be51 | 1f9729f2-ad46-4092-ae25-cfe6f5248763 | Reference | Default | DeleteMeButKeepReferences |
| `ModelReflectionChecker_EmailTemplate` | 63ef1b17-9a5d-4ef4-9697-f31988287661 | 1f9729f2-ad46-4092-ae25-cfe6f5248763 | Reference | Default | DeleteMeButKeepReferences |
| `EmailMessage_EmailTemplate` | e1a94f92-a454-43a2-9aae-e4836ce49b54 | 1f9729f2-ad46-4092-ae25-cfe6f5248763 | Reference | Default | DeleteMeButKeepReferences |
| `OutgoingEmailConfiguration_EmailAccount` | cb75b75b-b527-44c2-bc8c-2d6b170052fa | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | Reference | Both | DeleteMeButKeepReferences |
| `IncomingEmailConfiguration_EmailAccount` | 5d7769ca-db47-4732-85df-66f4d0f7083e | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | Reference | Both | DeleteMeButKeepReferences |
| `EmailMessage_EmailAccount` | e1a94f92-a454-43a2-9aae-e4836ce49b54 | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | Reference | Default | DeleteMeButKeepReferences |
| `IncomingServer_EmailProvider` | 022f2a74-02b5-46fc-aa5f-043606747482 | 2ef1e899-98a9-4092-a7be-28e3ede338d5 | Reference | Default | DeleteMeButKeepReferences |
| `OutgoingServer_EmailProvider` | c1b4b69e-e7e7-489b-a27f-14eac50c642b | 2ef1e899-98a9-4092-a7be-28e3ede338d5 | Reference | Default | DeleteMeButKeepReferences |
| `SelectedConfiguration_EmailProvider` | 8fb6af95-8fa3-40e4-a1f3-7b58895cb1f2 | 2ef1e899-98a9-4092-a7be-28e3ede338d5 | Reference | Both | DeleteMeButKeepReferences |
| `Pk12Certificate_EmailAccount` | e243b40f-dc57-4345-bbe8-fcdb35b220ac | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | Reference | Both | DeleteMeButKeepReferences |
| `EmailAccount_LDAPConfiguration` | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | 12281769-9510-4b7d-bbdb-bf465568f576 | Reference | Both | DeleteMeAndReferences |
| `EmailConnectorLog_EmailMessage` | 1b4d8ec2-3858-4499-9688-16cf9ef30d56 | e1a94f92-a454-43a2-9aae-e4836ce49b54 | Reference | Default | DeleteMeButKeepReferences |
| `EmailConnectorLog_EmailAccount` | 1b4d8ec2-3858-4499-9688-16cf9ef30d56 | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | Reference | Default | DeleteMeButKeepReferences |
| `ScopeSelected_OAuthProvider` | 6504e3c7-57c8-448c-95a2-a7abaec5ee90 | d432dd59-d0d5-43cb-92d7-510881b5d5b9 | Reference | Default | DeleteMeButKeepReferences |
| `EmailAccount_OAuthProvider` | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | d432dd59-d0d5-43cb-92d7-510881b5d5b9 | Reference | Default | DeleteMeButKeepReferences |
| `EmailAccount_OAuthToken` | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | 26717216-9b57-47a7-9888-ed0dd7ee847c | Reference | Both | DeleteMeAndReferences |
| `QueryString_OAuthProvider` | 10a4f465-fc79-4d49-9410-7076bc7aa994 | d432dd59-d0d5-43cb-92d7-510881b5d5b9 | Reference | Default | DeleteMeButKeepReferences |
| `EmailProvider_OAuthProvider` | 2ef1e899-98a9-4092-a7be-28e3ede338d5 | d432dd59-d0d5-43cb-92d7-510881b5d5b9 | Reference | Default | DeleteMeButKeepReferences |
| `OAuthNonce_EmailAccount` | 7c93b550-3b75-466b-b501-6fbc74a8e371 | fe2885ba-b8a7-4eef-9887-e404ed0c2a5f | Reference | Default | DeleteMeButKeepReferences |
| `EmailHeader_EmailMessage` | 3c6c5b97-5668-4e21-b5da-57ca67de401b | e1a94f92-a454-43a2-9aae-e4836ce49b54 | Reference | Default | DeleteMeButKeepReferences |
