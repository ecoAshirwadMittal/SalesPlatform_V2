# Domain Model

## Entities

### 📦 UnitTest
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `Result` | Enum(`UnitTesting.UnitTestResult`) | - | - | - |
| `ResultMessage` | StringAttribute | - | - | - |
| `LastStep` | StringAttribute | - | - | - |
| `LastRun` | DateTimeAttribute | - | - | - |
| `IsMf` | BooleanAttribute | - | false | - |
| `ReadableTime` | StringAttribute | 300 | - | - |
| `_dirty` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| UnitTesting.TestRunner | ❌ | ✅ | ✅ | ✅ | - |

### 📦 TestSuite
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Module` | StringAttribute | 200 | - | - |
| `LastRun` | DateTimeAttribute | - | - | - |
| `LastRunTime` | LongAttribute | - | 0 | - |
| `TestCount` | LongAttribute | - | 0 | - |
| `TestFailedCount` | LongAttribute | - | 0 | - |
| `AutoRollbackMFs` | BooleanAttribute | - | true | - |
| `Result` | Enum(`UnitTesting.UnitTestResult`) | - | - | - |
| `Prefix1` | StringAttribute | 10 | Test_ | - |
| `Prefix2` | StringAttribute | 10 | UT_ | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| UnitTesting.TestRunner | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `UnitTest_TestSuite` | 7460e4a4-e7e5-4f0f-9115-ef16c0fb677d | 1f4cb22d-0334-4187-b770-b79d06c3bced | Reference | Default | DeleteMeButKeepReferences |
