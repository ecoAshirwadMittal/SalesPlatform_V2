# Domain Model

## Entities

### 📦 Schedule
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `QueueName` | StringAttribute | 200 | - | Stored Calculate Attribute: FullName of the TaskQu |
| `MicroflowName` | StringAttribute | 200 | - | Stored Calculate Attribute FullName of the schedul |
| `Description` | StringAttribute | 200 | - | - |
| `Active` | BooleanAttribute | - | false | - |
| `Running` | BooleanAttribute | - | false | - |
| `LastRunTime` | DateTimeAttribute | - | - | DateTime when Schedule started the workflow for th |
| `LastStarted` | DateTimeAttribute | - | - | DateTime when the workflow execution started in th |
| `LastProcessed` | DateTimeAttribute | - | - | DateTime when it was detected that the workflow ex |
| `LastDuration` | DecimalAttribute | - | 0 | Mx 9.12.4: Calculated ProcessedQueuedTask.Duration |
| `NextRunTime` | DateTimeAttribute | - | - | - |
| `IntervalType` | Enum(`TaskQueueScheduler.Enum_IntervalType`) | - | - | - |
| `Interval` | IntegerAttribute | - | 0 | - |
| `ActiveFrom` | DateTimeAttribute | - | - | - |
| `ActiveUntil` | DateTimeAttribute | - | - | - |
| `RunningQueuedActions` | IntegerAttribute | - | 0 | Number of running QueuedActions for the Schedule |
| `OldMicroflowName` | StringAttribute | 200 | - | On Microflow rename we do not want schedules fetch |
| `RunAsUser` | StringAttribute | 200 | - | Username (System.User/Name) specifying the User th |

#### Event Handlers

- **Before Commit**: `TaskQueueScheduler.BCo_Schedule`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 TaskQueue
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `FullName` | StringAttribute | 200 | - | - |
| `ShortName` | StringAttribute | 200 | - | - |
| `Description` | StringAttribute | 200 | - | - |
| `AllowScheduling` | BooleanAttribute | - | false | Indicates if it is allowed to schedule reoccuring  |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 QueuedActionParameters
> QueuedActionParameters object provides the option to pass parameters. ProcessName is microflow to start ReferenceText is optional info text With Count, BatchSize and Offset we can break large actions into a number of batch processes each running an independent chunk. Param1..3 allow any string to be passed Filled with indentification we can even fetch needed parameter object(s) while start running on a background queue.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ProcessName` | StringAttribute | 200 | - | Either Process.Name or Process.MicroflowFullname |
| `ReferenceText` | StringAttribute | 400 | - | - |
| `Count` | IntegerAttribute | - | 0 | Total number of objects to process |
| `BatchSize` | IntegerAttribute | - | 0 | - |
| `Offset` | IntegerAttribute | - | 0 | - |
| `Param1` | StringAttribute | 2000 | - | - |
| `Param2` | StringAttribute | 200 | - | - |
| `Param3` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 QueuedAction
> You should only change the attribute 'ReferenceText' This field is there for easily searching the grids to find your objects

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ActionNumber` | AutoNumberAttribute | - | 1 | - |
| `QueueNumber` | IntegerAttribute | - | 0 | - |
| `StartTime` | DateTimeAttribute | - | - | - |
| `FinishTime` | DateTimeAttribute | - | - | - |
| `ReferenceText` | StringAttribute | 400 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PausedSchedule
> Schedules can be paused for some short period: Now until PausedUntil During this period: No new scheduled microflows are started. Unscheduled microflows are not actually placed on a queue but instead they are linked to PausedSchedule. After PausedUntil: New scheduled microflows are started again as normal. Unscheduled microflows linked to PausedSchedule are actually started

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `PausedUntil` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 PreviousInstance
> On ASU we need to pause the schedule queues until all previous instances are stopped.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `XASId` | StringAttribute | 50 | - | From System.XASInstance |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Microflow
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `CompleteName` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 BatchObject
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ObjectId` | LongAttribute | - | - | Mendix GUID of the object as String |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| TaskQueueScheduler.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 QueuedTask
> Used to store OQL query results (snapshot) on System.QueuedTask. NonPersistent QueuedTask entity allows us to work with relevant System.QueuedTask info without having to work with actual System.QueuedTask. Mendix recommends not using System.QueuedTask entity directly because that can be intensely used by Mendix internal processing of taskqueues.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MicroflowName` | StringAttribute | 200 | - | - |
| `QueueName` | StringAttribute | 200 | - | - |
| `ContextType` | StringAttribute | 200 | - | - |
| `NumberOfQueuedActions` | IntegerAttribute | - | 0 | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `Schedule_TaskQueue` | 0553e598-ed66-4787-b8de-0ec46946a800 | 4b7512e2-0913-469e-8e86-9b5513fa32b6 | Reference | Default | DeleteMeButKeepReferences |
| `QueuedActionParameters_QueuedAction` | c1d7b053-8eff-4e28-86da-6d9ad95c4303 | bdff4b8d-afba-4e31-90f6-77e7bce33d02 | Reference | Both | DeleteMeButKeepReferences |
| `QueuedAction_Schedule` | bdff4b8d-afba-4e31-90f6-77e7bce33d02 | 0553e598-ed66-4787-b8de-0ec46946a800 | Reference | Default | DeleteMeButKeepReferences |
| `Schedule_PausedSchedule` | 0553e598-ed66-4787-b8de-0ec46946a800 | 65e74d41-475f-4767-bd1d-ff900d0a3a4a | Reference | Default | DeleteMeButKeepReferences |
