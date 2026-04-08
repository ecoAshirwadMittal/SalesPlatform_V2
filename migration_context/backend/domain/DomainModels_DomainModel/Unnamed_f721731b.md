# Domain Model

## Entities

### 📦 DeepLink
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `Description` | StringAttribute | - | - | - |
| `Microflow` | StringAttribute | 200 | - | - |
| `ObjectType` | StringAttribute | 200 | - | - |
| `ObjectAttribute` | StringAttribute | 200 | - | - |
| `AllowGuests` | BooleanAttribute | - | false | - |
| `UseStringArgument` | BooleanAttribute | - | false | - |
| `SeparateGetParameters` | BooleanAttribute | - | false | - |
| `UseAsHome` | BooleanAttribute | - | false | - |
| `IndexPage` | StringAttribute | 100 | - | - |
| `HitCount` | IntegerAttribute | - | 0 | - |
| `ArgumentExample` | StringAttribute | 600 | - | - |
| `UseObjectArgument` | BooleanAttribute | - | false | - |
| `TrackHitCount` | BooleanAttribute | - | true | - |

#### Event Handlers

- **Before Commit**: `DeepLink.BCo_Deeplink_SetAttributes`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin | ✅ | ✅ | ✅ | ✅ | - |
| DeepLink.User | ❌ | ✅ | ✅ | ❌ | - |

#### Validation Rules

- **Name** (UniqueRuleInfo): "DeepLink name must be non-empty and unique"
- **Name** (RequiredRuleInfo): "DeepLink name must be non-empty and unique"
- **Microflow** (RequiredRuleInfo): "A microflow needs to be selected"
- **Name** (RegExRuleInfo): "Deeplink name cannot contain non alphanumeric characters"

### 📦 PendingLink
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `User` | StringAttribute | 200 | - | - |
| `Argument` | LongAttribute | - | 0 | - |
| `StringArgument` | StringAttribute | - | - | - |
| `SessionId` | StringAttribute | 50 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin, DeepLink.User | ❌ | ✅ | ✅ | ✅ | `[System.owner='[%CurrentUser%]']` |

### 📦 Microflow
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `FriendlyName` | StringAttribute | 200 | - | - |
| `UseStringArg` | BooleanAttribute | - | false | - |
| `UseObjectArgument` | BooleanAttribute | - | false | - |
| `Module` | StringAttribute | 200 | - | - |
| `Parameters` | StringAttribute | - | - | - |
| `NrOfParameters` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Entity
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Attribute
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DeeplinkResult
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SessionId` | StringAttribute | 200 | - | - |
| `Value1` | StringAttribute | 200 | - | - |
| `Value2` | StringAttribute | 200 | - | - |
| `Description` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin, DeepLink.User | ✅ | ✅ | ✅ | ❌ | - |

### 📦 DeepLinkURL
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `URL` | StringAttribute | - | - | - |
| `Path` | StringAttribute | - | - | - |
| `QueryString` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin, DeepLink.User | ✅ | ✅ | ✅ | ❌ | - |

### 📦 KeyValue
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Key` | StringAttribute | - | - | - |
| `Value` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin, DeepLink.User | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Microflow_Select_DataGrid
**Extends:** `System.Paging`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DeepLink.Admin, DeepLink.User | ✅ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `PendingLink_DeepLink` | 8cd16e74-1415-4bf7-8919-f7d07fbafb64 | 085d2d09-ce7f-45fe-9936-128547c61cff | Reference | Default | DeleteMeButKeepReferences |
| `param` | 70f3a881-5780-4992-80d8-b0c88f929c5e | 0829be96-a17c-4073-91ad-76825cb4bb91 | Reference | Default | DeleteMeButKeepReferences |
| `Attribute_Entity` | c16b370f-f586-4956-bae4-86b2f5b66463 | 0829be96-a17c-4073-91ad-76825cb4bb91 | Reference | Default | DeleteMeButKeepReferences |
| `DeepLink_Microflow` | 085d2d09-ce7f-45fe-9936-128547c61cff | 70f3a881-5780-4992-80d8-b0c88f929c5e | Reference | Default | DeleteMeButKeepReferences |
| `DeepLink_Entity` | 085d2d09-ce7f-45fe-9936-128547c61cff | 0829be96-a17c-4073-91ad-76825cb4bb91 | Reference | Default | DeleteMeButKeepReferences |
| `DeepLink_Attribute` | 085d2d09-ce7f-45fe-9936-128547c61cff | c16b370f-f586-4956-bae4-86b2f5b66463 | Reference | Default | DeleteMeButKeepReferences |
| `DeepLinkURL_DeepLink` | e494ec6c-8ad4-4e2c-92f5-dc43d1348b5c | 085d2d09-ce7f-45fe-9936-128547c61cff | Reference | Default | DeleteMeButKeepReferences |
