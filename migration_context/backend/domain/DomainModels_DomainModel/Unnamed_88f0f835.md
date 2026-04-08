# Domain Model

## Entities

### 📦 SSOConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IsSAMLLoggingEnabled` | BooleanAttribute | - | true | - |
| `AuthnContext` | Enum(`SAML20.TypeOfAuthnContext`) | - | EXACT | - |
| `IdPMetadataURL` | StringAttribute | 200 | - | - |
| `ReadIdPMetadataFromURL` | BooleanAttribute | - | true | - |
| `WizardMode` | BooleanAttribute | - | true | - |
| `CreateUsers` | BooleanAttribute | - | false | - |
| `CurrentWizardStep` | Enum(`SAML20.WizardStepCollection`) | - | Step1 | - |
| `Alias` | StringAttribute | 300 | - | - |
| `Active` | BooleanAttribute | - | false | - |
| `AllowIdpInitiatedAuthentication` | BooleanAttribute | - | false | - |
| `IdentifyingAssertionType` | Enum(`SAML20.IdentifyingAssertionType`) | - | IdP_Provided | - |
| `CustomIdentifyingAssertionName` | StringAttribute | - | - | - |
| `UseCustomLogicForProvisioning` | BooleanAttribute | - | false | - |
| `UseCustomAfterSigninLogic` | BooleanAttribute | - | false | - |
| `DisableNameIDPolicy` | BooleanAttribute | - | true | - |
| `EnableDelegatedAuthentication` | BooleanAttribute | - | false | If enabled, the 'DelegatedAuthentication' java act |
| `DelegatedAuthenticationURL` | StringAttribute | 500 | - | - |
| `EnableMobileAuthToken` | BooleanAttribute | - | false | If enabled, an auth token cookie will be set on lo |
| `MigratedToPrioritizedSAMLAuthnContexts` | BooleanAttribute | - | false | - |
| `ResponseProtocolBinding` | Enum(`SAML20.Enum_ProtocolBinding`) | - | POST_BINDING | - |
| `EnableAssertionConsumerServiceIndex` | Enum(`SAML20.Enum_AssertionConsumerServiceIndex`) | - | No | - |
| `AssertionConsumerServiceIndex` | IntegerAttribute | - | 0 | - |
| `EnableForceAuthentication` | BooleanAttribute | - | false | - |
| `UseEncryption` | BooleanAttribute | - | true | - |
| `EncryptionMethod` | Enum(`SAML20.EncryptionMethod`) | - | SHA256WithRSA | The value from this attribute is directly translat |
| `EncryptionKeyLength` | Enum(`SAML20.EncryptionKeyLength`) | - | _2048bit_Encryption | The length (in bits) of the keypair generated for  |
| `isUploadNewKeyPair` | BooleanAttribute | - | false | - |

#### Event Handlers

- **After Delete**: `SAML20.SSOConfigurationReloadConfig_ADe`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

#### Validation Rules

- **AssertionConsumerServiceIndex** (RangeRuleInfo): "Assertion Consumer Service index should be greather than or equal to 0"
- **AssertionConsumerServiceIndex** (RequiredRuleInfo): "Assertion Consumer Service index should contain a value"

### 📦 IdPMetadata
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SAMLRequest
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RequestID` | StringAttribute | 200 | - | - |
| `hasRequest` | Enum(`SAML20.YesNo`) | - | No | - |
| `hasResponse` | Enum(`SAML20.YesNo`) | - | No | - |
| `ReturnedPrincipal` | StringAttribute | 200 | - | - |
| `ResponseID` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SAMLResponse
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SAMLAuthnContext
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Description` | StringAttribute | 200 | - | - |
| `Value` | StringAttribute | 200 | - | - |
| `DefaultPriority` | IntegerAttribute | - | 0 | - |
| `Provisioned` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SSOLog
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Message` | StringAttribute | - | - | - |
| `LogonResult` | Enum(`SAML20.SSOLogResult`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ClaimMap
#### Event Handlers

- **After Commit**: `SAML20.ACo_ADe_ClaimMap`
- **After Delete**: `SAML20.ACo_ADe_ClaimMap`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Contact
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `contactType` | StringAttribute | 200 | - | - |
| `Company` | StringAttribute | 200 | - | - |
| `GivenName` | StringAttribute | 200 | - | - |
| `SurName` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ContactProperty
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_content_` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ServiceProperty
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_content_` | StringAttribute | 200 | - | - |
| `lang` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 AttributeConsumingService
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `index` | IntegerAttribute | - | 0 | - |
| `isDefault` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Attribute
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `NameFormat` | StringAttribute | 200 | - | - |
| `FriendlyName` | StringAttribute | 200 | - | - |
| `isRequired` | BooleanAttribute | - | false | - |
| `ManuallyCreated` | BooleanAttribute | - | false | - |
| `Updated` | BooleanAttribute | - | false | - |
| `IsInCommonFederation` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OrganizationProperty
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_content_` | StringAttribute | 200 | - | - |
| `lang` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Organization
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 NameIDFormat
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Description` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 EntityDescriptor
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `entityID` | StringAttribute | 200 | - | - |
| `validUntil` | DateTimeAttribute | - | - | - |
| `cacheDuration` | StringAttribute | 200 | - | - |
| `_ID` | StringAttribute | 200 | - | - |
| `Updated` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RoleDescriptor
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_ID` | StringAttribute | 200 | - | - |
| `validUntil` | DateTimeAttribute | - | - | - |
| `cacheDuration` | StringAttribute | 200 | - | - |
| `protocolSupportEnumeration` | StringAttribute | 200 | - | - |
| `errorURL` | StringAttribute | 200 | - | - |
| `AuthnRequestsSigned` | BooleanAttribute | - | false | - |
| `WantAuthnRequestsSigned` | BooleanAttribute | - | false | - |
| `WantAssertionsSigned` | BooleanAttribute | - | false | - |
| `RoleDescriptorType` | Enum(`SAML20.RoleDescriptorType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 EntitiesDescriptor
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `validUntil` | DateTimeAttribute | - | - | - |
| `cacheDuration` | StringAttribute | 200 | - | - |
| `_ID` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 200 | - | - |
| `Updated` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 KeyInfo
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_Id` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 KeyDescriptor
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `use` | StringAttribute | 20 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Endpoint
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Binding` | StringAttribute | 500 | - | - |
| `Location` | StringAttribute | 500 | - | - |
| `ResponseLocation` | StringAttribute | 500 | - | - |
| `index` | IntegerAttribute | - | 0 | - |
| `isDefault` | BooleanAttribute | - | false | - |
| `ServiceType` | Enum(`SAML20.ServiceType`) | - | - | - |
| `BindingType` | Enum(`SAML20.BindingType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 X509Certificate
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `IssuerName` | StringAttribute | 500 | - | - |
| `SerialNumber` | StringAttribute | - | - | - |
| `Subject` | StringAttribute | 500 | - | - |
| `ValidFrom` | DateTimeAttribute | - | - | - |
| `ValidUntil` | DateTimeAttribute | - | - | - |
| `Base64` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 KeyStore
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LastChangedOn` | DateTimeAttribute | - | - | - |
| `Alias` | StringAttribute | 200 | - | - |
| `RebuildKeyStore` | BooleanAttribute | - | false | - |
| `Password` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SPMetadata
**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `EntityID` | StringAttribute | 200 | - | - |
| `OrganizationName` | StringAttribute | 200 | - | - |
| `OrganizationDisplayName` | StringAttribute | 200 | - | - |
| `OrganizationURL` | StringAttribute | 200 | - | - |
| `ContactGivenName` | StringAttribute | 200 | - | - |
| `ContactSurName` | StringAttribute | 200 | - | - |
| `ContactEmailAddress` | StringAttribute | 200 | - | - |
| `ApplicationURL` | StringAttribute | 200 | - | - |
| `DoesEntityIdDifferFromAppURL` | BooleanAttribute | - | false | - |
| `LogAvailableDays` | IntegerAttribute | - | 7 | - |
| `UseEncryption` | BooleanAttribute | - | true | - |
| `EncryptionMethod` | Enum(`SAML20.EncryptionMethod`) | - | SHA256WithRSA | The value from this attribute is directly translat |
| `EncryptionKeyLength` | Enum(`SAML20.EncryptionKeyLength`) | - | _2048bit_Encryption | The length (in bits) of the keypair generated for  |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 AssertionAttribute
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Key` | StringAttribute | 200 | - | - |
| `Value` | StringAttribute | 2000 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 SAMLSession
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SessionID` | StringAttribute | 200 | - | - |
| `Username` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 LoginFeedback
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LoginAllowed` | BooleanAttribute | - | true | - |
| `FeedbackMessageHTML` | StringAttribute | - | - | This message is rendered as HTML |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ConfiguredSAMLAuthnContext
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Priority` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SPAttributeConsumingService
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ServiceName` | StringAttribute | 200 | - | - |
| `lang` | StringAttribute | 200 | - | - |
| `index` | IntegerAttribute | - | 0 | - |
| `isDefault` | BooleanAttribute | - | false | - |
| `isActive` | BooleanAttribute | - | false | - |
| `LoginType` | Enum(`SAML20.Enum_Attribute_Consuming_Login_Type`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

#### Validation Rules

- **ServiceName** (RequiredRuleInfo): "Service name should contain value"
- **index** (RequiredRuleInfo): "Attribute Consuming Service Index should contain value"
- **index** (RangeRuleInfo): "Attribute Consuming Service Index should be greather than or equal to 0"

### 📦 SPRequestedAttribute
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `isRequired` | BooleanAttribute | - | false | - |
| `Value` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| SAML20.Admin_ReadSAMLEntities | ❌ | ✅ | ✅ | ❌ | - |

#### Validation Rules

- **Name** (RequiredRuleInfo): "Name should contain a value"

### 📦 SSOMetadataLink
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ApplicationURL` | StringAttribute | 200 | - | - |
| `MetadataLink` | StringAttribute | 599 | - | - |
| `Alias` | StringAttribute | 300 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SAML20.Administrator, SAML20.Admin_ReadSAMLEntities | ✅ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `SSOConfiguration_SAMLAuthnContext` | 8255ce69-89a1-482b-9f5f-8929ca566b93 | 112c6c21-b032-4fb1-b78a-fdd478c51d1f | ReferenceSet | Default | DeleteMeButKeepReferences |
| `SSOConfiguration_IdPMetadata` | 8255ce69-89a1-482b-9f5f-8929ca566b93 | 9225a834-9cb0-48e5-82f5-80aef987d6bd | Reference | Both | DeleteMeAndReferences |
| `SAMLRequest_SAMLResponse` | aa20fdac-a109-4486-8666-e0ef5b3a7a92 | 0a3556d9-3fb2-40b7-ab8a-686f022bc904 | Reference | Both | DeleteMeAndReferences |
| `ClaimMap_SSOConfiguration` | b14eb3cb-c5d9-481c-9d09-5e49293ed694 | 8255ce69-89a1-482b-9f5f-8929ca566b93 | Reference | Default | DeleteMeButKeepReferences |
| `SAMLResponse_SSOConfiguration` | 0a3556d9-3fb2-40b7-ab8a-686f022bc904 | 8255ce69-89a1-482b-9f5f-8929ca566b93 | Reference | Default | DeleteMeButKeepReferences |
| `SAMLRequest_SSOConfiguration` | aa20fdac-a109-4486-8666-e0ef5b3a7a92 | 8255ce69-89a1-482b-9f5f-8929ca566b93 | Reference | Default | DeleteMeButKeepReferences |
| `ContactType_EmailAddress` | 82014da7-7a8a-4abc-af23-24ecd36a9a86 | ebebdc23-5e49-43bc-a07c-6b4e18279928 | Reference | Default | DeleteMeButKeepReferences |
| `ContactType_TelephoneNumber` | 82014da7-7a8a-4abc-af23-24ecd36a9a86 | ebebdc23-5e49-43bc-a07c-6b4e18279928 | Reference | Default | DeleteMeButKeepReferences |
| `AttributeConsumingServiceType_ServiceDescription` | 1db76374-a864-4ec3-8c20-17a4969357af | 7b6d7cf6-5bb6-4fba-ace0-ec8ed678e307 | ReferenceSet | Default | DeleteMeAndReferences |
| `AttributeConsumingServiceType_ServiceName` | 1db76374-a864-4ec3-8c20-17a4969357af | 7b6d7cf6-5bb6-4fba-ace0-ec8ed678e307 | Reference | Default | DeleteMeAndReferences |
| `ClaimMap_Attribute` | b14eb3cb-c5d9-481c-9d09-5e49293ed694 | 97d0001d-c909-44ef-8369-862d01e2a41a | Reference | Default | DeleteMeButKeepReferences |
| `SSOConfiguration_Attribute` | 8255ce69-89a1-482b-9f5f-8929ca566b93 | 97d0001d-c909-44ef-8369-862d01e2a41a | Reference | Default | DeleteMeAndReferences |
| `Attribute_AttributeConsumingService` | 97d0001d-c909-44ef-8369-862d01e2a41a | 1db76374-a864-4ec3-8c20-17a4969357af | Reference | Default | DeleteMeButKeepReferences |
| `Organization_OrganizationName` | a349a21a-7ab2-411a-a7bf-f1171b38ad19 | 142305f9-3f65-4cea-99ad-87f4c0498530 | Reference | Default | DeleteMeButKeepReferences |
| `Organization_OrganizationDisplayName` | a349a21a-7ab2-411a-a7bf-f1171b38ad19 | 142305f9-3f65-4cea-99ad-87f4c0498530 | Reference | Default | DeleteMeButKeepReferences |
| `Organization_OrganizationURL` | a349a21a-7ab2-411a-a7bf-f1171b38ad19 | 142305f9-3f65-4cea-99ad-87f4c0498530 | Reference | Default | DeleteMeButKeepReferences |
| `SSOConfiguration_PreferedEntityDescriptor` | 8255ce69-89a1-482b-9f5f-8929ca566b93 | 30da188f-1c64-4f64-b169-c63773106cde | Reference | Default | DeleteMeButKeepReferences |
| `ContactType_EntityDescriptor` | ebebdc23-5e49-43bc-a07c-6b4e18279928 | 30da188f-1c64-4f64-b169-c63773106cde | Reference | Default | DeleteMeButKeepReferences |
| `Organization_EntityDescriptor` | 142305f9-3f65-4cea-99ad-87f4c0498530 | 30da188f-1c64-4f64-b169-c63773106cde | Reference | Default | DeleteMeButKeepReferences |
| `RoleDescriptorType_ContactPerson` | ebebdc23-5e49-43bc-a07c-6b4e18279928 | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | Reference | Default | DeleteMeButKeepReferences |
| `AttributeConsumingService_RoleDescriptor` | 1db76374-a864-4ec3-8c20-17a4969357af | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | Reference | Default | DeleteMeButKeepReferences |
| `Attribute_RoleDescriptor` | 97d0001d-c909-44ef-8369-862d01e2a41a | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | Reference | Default | DeleteMeButKeepReferences |
| `RoleDescriptor_Organization` | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | 142305f9-3f65-4cea-99ad-87f4c0498530 | Reference | Default | DeleteMeAndReferences |
| `RoleDescriptor_EntityDescriptor` | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | 30da188f-1c64-4f64-b169-c63773106cde | Reference | Default | DeleteMeButKeepReferences |
| `EntityDescriptor_EntitiesDescriptor` | 30da188f-1c64-4f64-b169-c63773106cde | 9f7bffb6-8d4e-4fde-9aaa-41386fbdc2d5 | Reference | Default | DeleteMeButKeepReferences |
| `EntitiesDescriptor_IdPMetadata` | 9f7bffb6-8d4e-4fde-9aaa-41386fbdc2d5 | 9225a834-9cb0-48e5-82f5-80aef987d6bd | Reference | Default | DeleteMeButKeepReferences |
| `EntitiesDescriptor_KeyInfo` | 9f7bffb6-8d4e-4fde-9aaa-41386fbdc2d5 | 72f11c2a-299b-4a61-b1b6-199b1211cf9b | Reference | Default | DeleteMeAndReferences |
| `KeyInfo_EntityDescriptor` | 72f11c2a-299b-4a61-b1b6-199b1211cf9b | 30da188f-1c64-4f64-b169-c63773106cde | Reference | Default | DeleteMeButKeepReferences |
| `KeyDescriptor_RoleDescriptor` | df038ebb-d417-4213-89c3-671c02eb8c5a | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | Reference | Default | DeleteMeButKeepReferences |
| `KeyDescriptor_KeyInfo` | df038ebb-d417-4213-89c3-671c02eb8c5a | 72f11c2a-299b-4a61-b1b6-199b1211cf9b | Reference | Default | DeleteMeAndReferences |
| `SAMLRequest_Endpoint` | aa20fdac-a109-4486-8666-e0ef5b3a7a92 | 4996cca9-0024-4e8f-98aa-04939b4a635e | Reference | Default | DeleteMeButKeepReferences |
| `Endpoint_RoleDescriptor` | 4996cca9-0024-4e8f-98aa-04939b4a635e | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | Reference | Default | DeleteMeButKeepReferences |
| `NameIDFormat_RoleDescriptor` | 4f797630-7414-4245-8ba6-c4920294deb3 | 315a84f7-d8ee-4846-9fa2-237c1f9b09e1 | Reference | Default | DeleteMeButKeepReferences |
| `Attribute_IdPMetadata` | 97d0001d-c909-44ef-8369-862d01e2a41a | 9225a834-9cb0-48e5-82f5-80aef987d6bd | Reference | Default | DeleteMeButKeepReferences |
| `SPMetadata_KeyStore` | 142003c1-568e-4724-b9ff-3e9034523351 | 11069896-5e3d-4d8c-b9ee-08eb677e1594 | Reference | Both | DeleteMeAndReferences |
| `SSOConfiguration_NameIDFormat` | 8255ce69-89a1-482b-9f5f-8929ca566b93 | 4f797630-7414-4245-8ba6-c4920294deb3 | Reference | Default | DeleteMeButKeepReferences |
| `SAMLSession_SSOConfiguration` | 2a445e0d-1f58-4817-8747-a6c4f96a353a | 8255ce69-89a1-482b-9f5f-8929ca566b93 | Reference | Default | DeleteMeButKeepReferences |
| `KeyInfo_X509Certificate` | 72f11c2a-299b-4a61-b1b6-199b1211cf9b | 54230078-65e9-4c1d-b6fc-58bc8ce5c75e | ReferenceSet | Default | DeleteMeAndReferences |
| `ConfiguredSAMLAuthnContext_SSOConfiguration` | 92361ee0-42fe-4126-952f-dd6d5b2c6df8 | 8255ce69-89a1-482b-9f5f-8929ca566b93 | Reference | Default | DeleteMeButKeepReferences |
| `ConfiguredSAMLAuthnContext_SAMLAuthnContext` | 92361ee0-42fe-4126-952f-dd6d5b2c6df8 | 112c6c21-b032-4fb1-b78a-fdd478c51d1f | Reference | Default | DeleteMeButKeepReferences |
| `SPAttributeConsumingService_SSOConfiguration` | 496181f6-38f6-4207-82f9-bee66912c118 | 8255ce69-89a1-482b-9f5f-8929ca566b93 | Reference | Default | DeleteMeButKeepReferences |
| `SPRequestedAttribute_SPAttributeConsumingService` | 85b3f573-a5c3-44bc-b930-0644a88eccd7 | 496181f6-38f6-4207-82f9-bee66912c118 | Reference | Default | DeleteMeButKeepReferences |
| `SSOConfiguration_KeyStore` | 8255ce69-89a1-482b-9f5f-8929ca566b93 | 11069896-5e3d-4d8c-b9ee-08eb677e1594 | Reference | Both | DeleteMeAndReferences |
