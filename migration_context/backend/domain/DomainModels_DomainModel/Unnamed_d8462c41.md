# Domain Model

## Entities

### 📦 QueueCount
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `QueueName` | StringAttribute | 200 | - | - |
| `WaitingCount` | LongAttribute | - | 0 | - |
| `RunningCount` | LongAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueHelpers.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ProcessedQueueCount
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `QueueName` | StringAttribute | 200 | - | - |
| `CompletedCount` | LongAttribute | - | 0 | - |
| `UncompletedCount` | LongAttribute | - | 0 | - |
| `TotalCount` | LongAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueHelpers.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 CompletionStateCount
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompletionStateName` | StringAttribute | 200 | - | - |
| `Count` | LongAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueHelpers.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ChartContext
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueHelpers.Administrator | ✅ | ✅ | ✅ | ✅ | - |

