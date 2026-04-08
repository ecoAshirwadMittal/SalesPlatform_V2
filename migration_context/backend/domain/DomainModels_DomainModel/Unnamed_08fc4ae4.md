# Domain Model

> Latest model change: 23-04-2010 Changes: 23-04-2010 Java: - Bug fixes for syncing inherited references. - Bug fix for removing old object types Forms: - Added inherited objects to the objecttype popup - Fixed some of the dbl click actions - Translated the missing captions 12-04-2010 All: - Converted the application to 2.5, removed all (compile) errors and implemented all new 2.5 features to make the module even better

## Entities

### 📦 MxObjectType
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompleteName` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 200 | - | - |
| `Module` | StringAttribute | 200 | - | - |
| `ReadableName` | StringAttribute | 400 | - | - |
| `PersistenceType` | Enum(`MxModelReflection.PersistenceType`) | - | Persistable | - |

#### Event Handlers

- **Before Commit**: `MxModelReflection.BCo_MxObjectType`
- **Before Delete**: `MxModelReflection.BDe_MxObjectType`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |

### 📦 MxObjectMember
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `AttributeName` | StringAttribute | 200 | - | - |
| `AttributeType` | StringAttribute | 200 | - | - |
| `AttributeTypeEnum` | Enum(`MxModelReflection.PrimitiveTypes`) | - | - | - |
| `CompleteName` | StringAttribute | 400 | - | - |
| `DescriptiveName` | StringAttribute | 200 | - | - |
| `FieldLength` | IntegerAttribute | - | - | - |
| `IsVirtual` | BooleanAttribute | - | false | - |

#### Event Handlers

- **Before Commit**: `MxModelReflection.BCo_MxObjectMember_CreateCompleteMemberName`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |

### 📦 MxObjectReference
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompleteName` | StringAttribute | 200 | - | - |
| `Module` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 200 | - | - |
| `ReadableName` | StringAttribute | 200 | - | - |
| `ReferenceType` | Enum(`MxModelReflection.ReferenceType`) | - | - | - |
| `AssociationOwner` | Enum(`MxModelReflection.AssociationOwner`) | - | _Default | - |
| `ParentEntity` | StringAttribute | 200 | - | - |

#### Event Handlers

- **Before Commit**: `MxModelReflection.BCo_MxObjectReference`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Microflows
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `Module` | StringAttribute | 200 | - | - |
| `CompleteName` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxObjectEnum
**Extends:** `MxModelReflection.MxObjectMember`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |

### 📦 MxObjectEnumValue
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.Readonly | ❌ | ✅ | ✅ | ❌ | - |
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxObjectEnumCaptions
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Caption` | StringAttribute | 200 | - | - |
| `LanguageCode` | StringAttribute | 8 | - | - |
| `LanguageName` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ValueType
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `TypeEnum` | Enum(`MxModelReflection.PrimitiveTypes`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Parameter
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Token
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Token` | StringAttribute | 50 | - | - |
| `Prefix` | StringAttribute | 3 | {% | - |
| `Suffix` | StringAttribute | 3 | %} | - |
| `CombinedToken` | StringAttribute | 56 | - | - |
| `Description` | StringAttribute | 300 | - | - |
| `MetaModelPath` | StringAttribute | 1000 | - | - |
| `TokenType` | Enum(`MxModelReflection.TokenType`) | - | - | - |
| `Status` | Enum(`MxModelReflection.Status`) | - | Invalid | - |
| `FindObjectStart` | StringAttribute | 200 | - | - |
| `FindObjectReference` | StringAttribute | 200 | - | - |
| `FindReference` | StringAttribute | 200 | - | - |
| `FindMember` | StringAttribute | 200 | - | - |
| `FindMemberReference` | StringAttribute | 200 | - | - |
| `IsOptional` | BooleanAttribute | - | false | - |
| `DisplayPattern` | StringAttribute | 50 | - | - |

#### Indexes

- **Index**: `c9fba28c-7891-42e6-8396-3457700f0144` (Ascending)

#### Event Handlers

- **Before Commit**: `MxModelReflection.BCo_Token`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator, MxModelReflection.TokenUser | ✅ | ✅ | ✅ | ✅ | - |
| MxModelReflection.Readonly | ❌ | ✅ | ✅ | ❌ | - |

#### Validation Rules

- **Prefix** (RequiredRuleInfo): "Prefix is required"
- **Suffix** (RequiredRuleInfo): "Suffix is required"
- **Token** (RequiredRuleInfo): "Token is required"

### 📦 Module
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ModuleName` | StringAttribute | 200 | - | - |
| `SynchronizeObjectsWithinModule` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator | ✅ | ✅ | ✅ | ✅ | - |
| MxModelReflection.Readonly, MxModelReflection.TokenUser | ❌ | ✅ | ✅ | ❌ | - |

### 📦 DbSizeEstimate
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `NrOfRecords` | IntegerAttribute | - | - | - |
| `CalculatedSizeInBytes` | LongAttribute | - | 0 | - |
| `CalculatedSizeInKiloBytes` | LongAttribute | - | 0 | - |
| `FindObjectType` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator, MxModelReflection.Readonly | ❌ | ✅ | ✅ | ❌ | - |

### 📦 InheritsFromContainer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Summary` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 TestPattern
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DisplayPattern` | StringAttribute | 50 | - | - |
| `AttributeTypeEnum` | Enum(`MxModelReflection.AttributeTypes`) | - | - | - |
| `BooleanAttribute` | BooleanAttribute | - | false | - |
| `DecimalAttribute` | DecimalAttribute | - | 1234.56787 | - |
| `DateTimeAttribute` | DateTimeAttribute | - | [%CurrentDateTime%] | - |
| `IntegerAttribute` | IntegerAttribute | - | 123456787 | - |
| `LongAttribute` | LongAttribute | - | 123456787 | - |
| `StringAttribute` | StringAttribute | 200 | My Text 1234.56787 | - |
| `Result` | StringAttribute | 500 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator, MxModelReflection.TokenUser | ✅ | ✅ | ✅ | ✅ | - |

### 📦 StringValue
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Value` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| MxModelReflection.ModelAdministrator, MxModelReflection.Readonly, MxModelReflection.TokenUser | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `MxObjectMember_MxObjectType` | 0b4cc9a7-50e2-4a28-9f58-6752242903d2 | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | Reference | Default | DeleteMeButKeepReferences |
| `MxObjectReference_MxObjectType` | 7b57d70f-8395-4171-8cee-3a1f19d2330e | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `Values` | a83a8590-187a-420b-a2b6-56b038dbe879 | 576fbebd-410d-48ce-a124-2719439d84a3 | ReferenceSet | Default | DeleteMeAndReferences |
| `Captions` | 576fbebd-410d-48ce-a124-2719439d84a3 | 8c404ccf-58f9-47a2-a1af-ea27036930b8 | ReferenceSet | Default | DeleteMeAndReferences |
| `MxObjectType_SubClassOf_MxObjectType` | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `MxObjectMember_Type` | 0b4cc9a7-50e2-4a28-9f58-6752242903d2 | 775def5e-5d2e-41c9-a31f-8a51536a74b4 | Reference | Default | DeleteMeButKeepReferences |
| `Microflows_InputParameter` | cd74683b-ff99-4eaa-b831-9ff0f33ba309 | ef5585fb-a3a4-4cdd-a6b7-27e573d06dbf | ReferenceSet | Default | DeleteMeAndReferences |
| `Microflows_Output_Type` | cd74683b-ff99-4eaa-b831-9ff0f33ba309 | 775def5e-5d2e-41c9-a31f-8a51536a74b4 | Reference | Default | DeleteMeButKeepReferences |
| `Parameter_ValueType` | ef5585fb-a3a4-4cdd-a6b7-27e573d06dbf | 775def5e-5d2e-41c9-a31f-8a51536a74b4 | Reference | Default | DeleteMeButKeepReferences |
| `Parameter_MxObjectType` | ef5585fb-a3a4-4cdd-a6b7-27e573d06dbf | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | Reference | Default | DeleteMeButKeepReferences |
| `Token_MxObjectMember` | df7c4aa4-f74c-4743-90fc-5e1f6bb587f4 | 0b4cc9a7-50e2-4a28-9f58-6752242903d2 | Reference | Default | DeleteMeButKeepReferences |
| `Token_MxObjectType_Start` | df7c4aa4-f74c-4743-90fc-5e1f6bb587f4 | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | Reference | Default | DeleteMeButKeepReferences |
| `Token_MxObjectType_Referenced` | df7c4aa4-f74c-4743-90fc-5e1f6bb587f4 | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | Reference | Default | DeleteMeButKeepReferences |
| `Token_MxObjectReference` | df7c4aa4-f74c-4743-90fc-5e1f6bb587f4 | 7b57d70f-8395-4171-8cee-3a1f19d2330e | Reference | Default | DeleteMeButKeepReferences |
| `ValueType_MxObjectType` | 775def5e-5d2e-41c9-a31f-8a51536a74b4 | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | Reference | Default | DeleteMeButKeepReferences |
| `MxObjectReference_MxObjectType_Child` | 7b57d70f-8395-4171-8cee-3a1f19d2330e | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `MxObjectReference_MxObjectType_Parent` | 7b57d70f-8395-4171-8cee-3a1f19d2330e | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `Microflows_Module` | cd74683b-ff99-4eaa-b831-9ff0f33ba309 | 8d69edcf-473a-49fb-8c0c-67fdef4704fe | Reference | Default | DeleteMeButKeepReferences |
| `MxObjectType_Module` | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | 8d69edcf-473a-49fb-8c0c-67fdef4704fe | Reference | Default | DeleteMeButKeepReferences |
| `MxObjectReference_Module` | 7b57d70f-8395-4171-8cee-3a1f19d2330e | 8d69edcf-473a-49fb-8c0c-67fdef4704fe | Reference | Default | DeleteMeButKeepReferences |
| `DbSizeEstimate_MxObjectType` | ede67b5a-0291-483e-88e5-5e4310ddb346 | 60093b7b-4fab-4064-b3e1-e163ff5b8670 | Reference | Default | DeleteMeButKeepReferences |
