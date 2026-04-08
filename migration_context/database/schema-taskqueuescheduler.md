## taskqueuescheduler

### schedule
- **Table:** `taskqueuescheduler$schedule` | **Rows:** 45
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 135389463797915366, 135389463797889894, 135389463798222558 |
| queuename | character varying(200) | Yes | TaskQueueScheduler.Schedule..., EcoATM_PWS.TaskQueue_SendPr..., TaskQueueScheduler.Schedule... |
| microflowname | character varying(200) | Yes | DeepLink.ClearOldPendingLinks, EcoATM_PWS.SUB_SendDevicePr..., EcoATM_PWSIntegration.ACT_P... |
| description | character varying(200) | Yes | Run once every week to dele..., Scheduled job to pull ATP j..., Scheduled job to pull order... |
| active | boolean | Yes | false |
| running | boolean | Yes | true, false |
| lastruntime | timestamp without time zone | Yes | 2026-02-18 01:44:00.041, 2026-02-19 19:23:00.873, 2025-10-06 13:58:16.585 |
| laststarted | timestamp without time zone | Yes | 2026-02-19 19:33:59.427, 2026-02-20 02:28:00.456, 2026-02-19 01:30:37.423 |
| lastprocessed | timestamp without time zone | Yes | 2026-02-18 22:00:09.042, 2026-02-18 21:19:11.42, 2026-02-19 20:01:51.066 |
| lastduration | numeric | Yes | 106204.00000000, 8939869.00000000, 4318.00000000 |
| nextruntime | timestamp without time zone | Yes | 2026-02-20 19:00:00, 2026-02-19 20:10:00, 2026-02-26 06:00:00 |
| intervaltype | character varying(6) | Yes | Day, Minute, Week |
| interval | integer | Yes | 15, 3, 30 |
| activefrom | timestamp without time zone | Yes | 2025-04-16 20:53:39.971, 2025-08-21 20:40:48.264, 2025-09-01 20:31:29.895 |
| activeuntil | timestamp without time zone | Yes | 2055-09-01 05:00:00, 2055-01-14 06:00:00, 2055-04-16 05:00:00 |
| runningqueuedactions | integer | Yes | 0, 7, 8 |
| oldmicroflowname | character varying(200) | Yes | DeepLink.ClearOldPendingLinks, EcoATM_PWS.SUB_SendDevicePr..., EcoATM_PWSIntegration.ACT_P... |
| runasuser | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes | 2025-04-21 17:24:00.356, 2025-04-18 00:35:00.429, 2025-10-28 20:59:00.204 |
| changeddate | timestamp without time zone | Yes | 2026-02-20 02:12:00.722, 2026-02-20 02:28:00.661, 2025-05-05 17:00:00.977 |
| system$changedby | bigint | Yes | 9851624184950013, 23925373020815588 |
- **PK:** id

### schedule_taskqueue
- **Table:** `taskqueuescheduler$schedule_taskqueue` | **Rows:** 45
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| taskqueuescheduler$scheduleid | bigint | No | 135389463797915366, 135389463797889894, 135389463798222558 |
| taskqueuescheduler$taskqueueid | bigint | No | 138767163518353626, 138767163518430375, 138767163518494351 |
- **PK:** taskqueuescheduler$scheduleid, taskqueuescheduler$taskqueueid

### taskqueue
- **Table:** `taskqueuescheduler$taskqueue` | **Rows:** 18
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 138767163518443257, 138767163518354193, 138767163518366441 |
| name | character varying(200) | Yes | TaskQueue_BidRoundFileCreation, Task_CreateAuctionFilters, ScheduledEventsQueue |
| fullname | character varying(200) | Yes | TaskQueueScheduler.Schedule..., EcoATM_PWS.TaskQueue_SendPr..., TaskQueueScheduler.Schedule... |
| shortname | character varying(200) | Yes | Batch1, TaskQueue_BidRoundFileCreation, Task_CreateAuctionFilters |
| description | character varying(200) | Yes | Third queue for scheduler, Second queue for scheduler, Queue with 3 threads for pa... |
| allowscheduling | boolean | Yes | true, false |
- **PK:** id

### pausedschedule
- **Table:** `taskqueuescheduler$pausedschedule` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| pauseduntil | timestamp without time zone | Yes |  |
- **PK:** id

### previousinstance
- **Table:** `taskqueuescheduler$previousinstance` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| xasid | character varying(50) | Yes |  |
- **PK:** id

### queuedaction
- **Table:** `taskqueuescheduler$queuedaction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| actionnumber | bigint | Yes |  |
| queuenumber | integer | Yes |  |
| starttime | timestamp without time zone | Yes |  |
| finishtime | timestamp without time zone | Yes |  |
| referencetext | character varying(400) | Yes |  |
- **PK:** id

### queuedaction_schedule
- **Table:** `taskqueuescheduler$queuedaction_schedule` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| taskqueuescheduler$queuedactionid | bigint | No |  |
| taskqueuescheduler$scheduleid | bigint | No |  |
- **PK:** taskqueuescheduler$queuedactionid, taskqueuescheduler$scheduleid

### queuedactionparameters
- **Table:** `taskqueuescheduler$queuedactionparameters` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| processname | character varying(200) | Yes |  |
| referencetext | character varying(400) | Yes |  |
| count | integer | Yes |  |
| batchsize | integer | Yes |  |
| offset | integer | Yes |  |
| param1 | character varying(2000) | Yes |  |
| param2 | character varying(200) | Yes |  |
| param3 | character varying(200) | Yes |  |
- **PK:** id

### queuedactionparameters_queuedaction
- **Table:** `taskqueuescheduler$queuedactionparameters_queuedaction` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| taskqueuescheduler$queuedactionparametersid | bigint | No |  |
| taskqueuescheduler$queuedactionid | bigint | No |  |
- **PK:** taskqueuescheduler$queuedactionparametersid, taskqueuescheduler$queuedactionid

### schedule_pausedschedule
- **Table:** `taskqueuescheduler$schedule_pausedschedule` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| taskqueuescheduler$scheduleid | bigint | No |  |
| taskqueuescheduler$pausedscheduleid | bigint | No |  |
- **PK:** taskqueuescheduler$scheduleid, taskqueuescheduler$pausedscheduleid

---
