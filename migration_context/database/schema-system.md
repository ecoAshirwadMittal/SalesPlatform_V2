## system

### autocommitentry
- **Table:** `system$autocommitentry` | **Rows:** 1,551,217
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 7036874417805059, 7036874499440010, 7036874499440208 |
| sessionid | character varying(36) | Yes | 163ddded-271d-4bbb-a956-b70..., 1c10e4e3-987b-4fff-a7a4-9fe..., 2b9a55d5-9f09-477c-96e3-599... |
| objectid | bigint | Yes | 152277962400465071, 152277962400465163, 152277962400465317 |
| createddate | timestamp without time zone | Yes | 2025-08-21 21:24:22.507, 2025-09-19 15:46:00.482, 2026-01-12 15:33:32.899 |
- **PK:** id

### processedqueuetask
- **Table:** `system$processedqueuetask` | **Rows:** 106,131
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 12666374066130098, 12666374066130292, 12666374066130323 |
| sequence | bigint | Yes | 2548248, 2603884, 2650838 |
| status | character varying(12) | Yes | Failed, Aborted, Completed |
| queueid | character varying(36) | Yes | 57e8d667-ae23-4c16-9bfc-63f..., 9a760161-9130-4cc6-a86a-c7c..., d38e291b-1620-4f21-86a6-765... |
| queuename | character varying(200) | Yes | AuctionUI.Task_queue_Round3..., TaskQueueScheduler.Schedule..., EcoATM_PWS.TaskQueue_SendPr... |
| contexttype | character varying(14) | Yes | User, ScheduledEvent |
| contextdata | text | Yes |  |
| microflowname | character varying(200) | Yes | DeepLink.ClearOldPendingLinks, EcoATM_PWS.SUB_SendDevicePr..., EcoATM_PWSIntegration.ACT_P... |
| useractionname | character varying(200) | Yes | ClusterManagement-CleanupOf..., ClusterManagement-UnblockUsers, ClusterManagement-CleanupSc... |
| arguments | text | Yes |  |
| xasid | character varying(50) | Yes | 2c32b7dc-4a43-4fb6-a354-3b8..., d89524af-982d-48be-b1c0-b11..., d65ee834-75e6-4524-813c-d3c... |
| threadid | bigint | Yes | 103, 104, 108 |
| created | timestamp without time zone | Yes | 2026-02-15 14:04:00.673, 2026-02-16 05:35:41.582, 2026-02-15 11:03:00.862 |
| startat | timestamp without time zone | Yes | 2026-02-18 15:40:17.917, 2026-02-16 04:55:41.191, 2026-02-18 01:13:41.184 |
| started | timestamp without time zone | Yes | 2026-02-13 20:18:46.026, 2026-02-18 00:58:11.877, 2026-02-18 19:55:26.449 |
| finished | timestamp without time zone | Yes | 2026-02-17 07:00:11.339, 2026-02-16 05:35:41.582, 2026-02-15 12:17:00.629 |
| duration | bigint | Yes | 247, 41711, 43567 |
| retried | bigint | Yes | 0, 1, 2 |
| errormessage | text | Yes |  |
| scheduledeventname | character varying(200) | Yes | ClusterManagement-CleanupOf..., MicrosoftGraph.SCE_Subscrip..., AuctionUI.Scheduled_event_I... |
| system$owner | bigint | Yes | 23925373021007967, 23925373022646096, 23925373021506978 |
- **PK:** id

### filedocument
- **Table:** `system$filedocument` | **Rows:** 22,988
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 61643019902154064, 61643019900210017, 61643019903269633 |
| fileid | bigint | Yes | 144643, 117087, 143193 |
| name | character varying(400) | Yes | KB_2026_Wk04_Round1.xlsx, DW25_2025_Wk12_Round1.xlsx, DW250_2026_Wk03_Round1.xlsx |
| deleteafterdownload | boolean | Yes | true, false |
| contents | bytea | Yes |  |
| hascontents | boolean | Yes | true, false |
| size | bigint | Yes | 205048, 682333, 611126 |
| __filename__ | bigint | Yes | 0 |
| __uuid__ | character varying(36) | Yes | 00014821-062c-41be-8d79-afa..., 000f4c33-3ca0-4315-931e-916..., 000fbbf0-0252-4821-8f76-cd0... |
| createddate | timestamp without time zone | Yes | 2025-01-24 16:27:37.71, 2025-03-07 09:18:55.535, 2025-10-17 05:57:41.24 |
| changeddate | timestamp without time zone | Yes | 2025-10-28 03:35:45.66, 2026-01-19 15:44:24.389, 2025-08-19 02:57:36.336 |
| submetaobjectname | character varying(255) | Yes | AuctionUI.AllBidsDoc, AuctionUI.BidDataDoc, AuctionUI.RoundThreeBidData... |
| system$owner | bigint | Yes | 23925373022671603, 23925373022147278, 23925373021023007 |
| system$changedby | bigint | Yes | 23925373022671603, 23925373022147278, 23925373021023007 |
- **PK:** id

### scheduledeventinformation
- **Table:** `system$scheduledeventinformation` | **Rows:** 9,712
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 5629499750133552, 5629499750145993, 5629499749932820 |
| name | character varying(200) | Yes | SAML20.SE_LogCleanUp, TaskQueueScheduler.ScE_Sche..., DeepLink.CleanUp |
| description | text | Yes |  |
| starttime | timestamp without time zone | Yes | 2026-02-15 02:09:00.84, 2026-02-18 07:18:00.935, 2026-02-14 12:36:00.489 |
| endtime | timestamp without time zone | Yes | 2026-02-15 21:56:00.362, 2026-02-14 14:33:00.942, 2026-02-16 20:42:00.447 |
| status | character varying(9) | Yes | Error, Stopped, Completed |
- **PK:** id

### userroles
- **Table:** `system$userroles` | **Rows:** 1,123
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$userid | bigint | No | 23925373022671603, 23925373022147278, 23925373021023007 |
| system$userroleid | bigint | No | 9570149208162666, 9570149208188158, 9570149208188355 |
- **PK:** system$userid, system$userroleid

### user
- **Table:** `system$user` | **Rows:** 1,101
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 23925373022671603, 23925373022147278, 23925373021021319 |
| name | character varying(100) | Yes | 123buywholesale@gmail.com, 15017997789@163.com, 30024149@qq.com |
| password | character varying(200) | Yes | {BCrypt}$2a$10$VGGUrT25OlhB..., {BCrypt}$2a$10$SpEeLA5yyyXx..., {BCrypt}$2a$10$S/xM34Za1A9L... |
| lastlogin | timestamp without time zone | Yes | 2025-12-11 16:46:36.262, 2026-02-16 10:44:03.511, 2026-01-13 22:02:05.688 |
| blocked | boolean | Yes | false |
| blockedsince | timestamp without time zone | Yes |  |
| active | boolean | Yes | true, false |
| failedlogins | integer | Yes | 0, 2, 1 |
| webserviceuser | boolean | Yes | false |
| isanonymous | boolean | Yes | true, false |
| createddate | timestamp without time zone | Yes | 2024-12-31 23:17:10.596, 2025-06-06 15:52:23.549, 2025-02-13 22:53:49.108 |
| changeddate | timestamp without time zone | Yes | 2026-02-06 19:27:23.689, 2026-02-06 07:13:39.927, 2026-02-17 16:44:52.161 |
| submetaobjectname | character varying(255) | Yes | System.User, Administration.Account, EcoATM_UserManagement.EcoAT... |
| system$changedby | bigint | Yes | 23925373022658906, 23925373022646096, 23925373022173026 |
| system$owner | bigint | Yes | 23925373022671603, 23925373022147278, 23925373021021319 |
- **PK:** id

### user_timezone
- **Table:** `system$user_timezone` | **Rows:** 1,101
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$userid | bigint | No | 23925373022671603, 23925373022147278, 23925373021023007 |
| system$timezoneid | bigint | No | 9288674231473512 |
- **PK:** system$userid, system$timezoneid

### user_language
- **Table:** `system$user_language` | **Rows:** 1,090
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$userid | bigint | No | 23925373022671603, 23925373022147278, 23925373021021319 |
| system$languageid | bigint | No | 13792273858822380 |
- **PK:** system$userid, system$languageid

### timezone
- **Table:** `system$timezone` | **Rows:** 519
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 9288674231496699, 9288674231471996, 9288674231471743 |
| code | character varying(50) | Yes | America/Cayenne, Asia/Novokuznetsk, Europe/Copenhagen |
| description | character varying(100) | Yes | (GMT-03:00) Argentina/Rio G..., (GMT-04:00) Santiago/America, (GMT-01:00) Cape Verde/Atla... |
| rawoffset | integer | Yes | 16200000, -12600000, 31500000 |
- **PK:** id

### queuedtask
- **Table:** `system$queuedtask` | **Rows:** 31
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 4503600613302648, 4503600613301746, 4503600613302865 |
| sequence | bigint | Yes | 7677513, 7677514, 7677470 |
| status | character varying(12) | Yes | Idle |
| queueid | character varying(36) | Yes | 59651013-f9bc-4fc1-b435-276..., 068a9ed9-e0ff-4227-949d-603... |
| queuename | character varying(200) | Yes | System.ScheduledEventsQueue, EcoATM_PWSIntegration.pws_s... |
| contexttype | character varying(14) | Yes | ScheduledEvent, User |
| contextdata | text | Yes |  |
| microflowname | character varying(200) | Yes | DocumentGeneration.SE_Docum..., EcoATM_PWS.SUB_ImeiData_Upd..., TaskQueueScheduler.ACT_Hand... |
| useractionname | character varying(200) | Yes | ClusterManagement-CleanupOf..., ClusterManagement-UnblockUsers, ClusterManagement-CleanupSc... |
| arguments | text | Yes |  |
| xasid | character varying(50) | Yes |  |
| threadid | bigint | Yes |  |
| created | timestamp without time zone | Yes | 2026-02-20 02:11:43.591, 2026-02-20 02:27:20.94, 2026-02-20 02:11:34.782 |
| startat | timestamp without time zone | Yes | 2026-02-20 02:31:41.184, 2026-02-20 02:33:45.191, 2026-02-20 02:31:41.191 |
| started | timestamp without time zone | Yes |  |
| retried | bigint | Yes | 1, 0 |
| retry | character varying(200) | Yes |  |
| scheduledeventname | character varying(200) | Yes | ClusterManagement-CleanupOf..., MicrosoftGraph.SCE_Subscrip..., DocumentGeneration.SE_Docum... |
| system$owner | bigint | Yes | 9851624184950013 |
- **PK:** id

### grantableroles
- **Table:** `system$grantableroles` | **Rows:** 13
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$userroleid1 | bigint | No | 9570149208188355, 9570149208175484, 9570149208162509 |
| system$userroleid2 | bigint | No | 9570149208162666, 9570149208188158, 9570149208188355 |
- **PK:** system$userroleid1, system$userroleid2

### userrole
- **Table:** `system$userrole` | **Rows:** 11
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 9570149208162666, 9570149208188158, 9570149208175243 |
| modelguid | character varying(36) | Yes | eafc9213-d2da-4cce-b4a7-6b1..., 01bccfa0-772a-4722-be46-5e9..., 50b2640b-28a0-4ce8-9c4c-bbd... |
| name | character varying(100) | Yes | Bidder, SalesOps, ecoAtmDirectAdmin |
| description | character varying(1000) | Yes |  |
- **PK:** id

### taskqueuetoken
- **Table:** `system$taskqueuetoken` | **Rows:** 4
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 7318349394477197, 7318349394490021, 7318349394515615 |
| queuename | character varying(200) | Yes | AuctionUI.TaskQueue_BidRoun..., EcoATM_PWS.TaskQueue_SendCa..., TaskQueueScheduler.Batch1Cl... |
| xasid | character varying(50) | Yes |  |
| validuntil | timestamp without time zone | Yes |  |
- **PK:** id

### session
- **Table:** `system$session` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 6755399484947268, 6755399484947443, 6755399484947535 |
| sessionid | character varying(50) | Yes | 1f4664ec-0480-4bfd-b16d-aeb..., 4f75fcfe-5569-488d-bc54-c02..., ed31523e-a8be-4526-8944-400... |
| csrftoken | character varying(36) | Yes | 2112f332-e881-4843-bc29-045..., d443dbc8-688f-4115-986a-b5f..., eae2abfb-ba22-42e0-a2e5-062... |
| lastactive | timestamp without time zone | Yes | 2026-02-20 02:27:22.011, 2026-02-20 02:27:38.539, 2026-02-20 02:28:56.543 |
| longlived | boolean | Yes | false |
| readonlyhashkey | character varying(36) | Yes | 8b1dacdd-c9b2-41f8-a412-087..., 935c3750-c2ef-441d-9b75-c00..., c7bed0c1-ecfa-47d2-b8d6-680... |
| lastactionexecution | timestamp without time zone | Yes | 2026-02-20 02:27:22.028, 2026-02-20 02:27:22.032 |
| createddate | timestamp without time zone | Yes | 2026-02-20 02:27:21.82, 2026-02-20 02:27:22.007, 2026-02-20 02:27:39.466 |
- **PK:** id

### session_user
- **Table:** `system$session_user` | **Rows:** 3
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$sessionid | bigint | No | 6755399484947268, 6755399484947443, 6755399484947535 |
| system$userid | bigint | No | 281475010490033, 281475010490234, 9851624184950013 |
- **PK:** system$sessionid, system$userid

### language
- **Table:** `system$language` | **Rows:** 1
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No | 13792273858822380 |
| code | character varying(20) | Yes | en_US |
| description | character varying(200) | Yes | English, United States |
- **PK:** id

### backgroundjob
- **Table:** `system$backgroundjob` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| jobid | bigint | Yes |  |
| starttime | timestamp without time zone | Yes |  |
| endtime | timestamp without time zone | Yes |  |
| result | text | Yes |  |
| successful | boolean | Yes |  |
- **PK:** id

### backgroundjob_session
- **Table:** `system$backgroundjob_session` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$backgroundjobid | bigint | No |  |
| system$sessionid | bigint | No |  |
- **PK:** system$backgroundjobid, system$sessionid

### backgroundjob_xasinstance
- **Table:** `system$backgroundjob_xasinstance` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$backgroundjobid | bigint | No |  |
| system$xasinstanceid | bigint | No |  |
- **PK:** system$backgroundjobid, system$xasinstanceid

### changehash
- **Table:** `system$changehash` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| objectid | bigint | Yes |  |
| attribute | character varying(200) | Yes |  |
| hash | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
- **PK:** id

### changehash_session
- **Table:** `system$changehash_session` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$changehashid | bigint | No |  |
| system$sessionid | bigint | No |  |
- **PK:** system$changehashid, system$sessionid

### image
- **Table:** `system$image` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| publicthumbnailpath | character varying(500) | Yes |  |
| enablecaching | boolean | Yes |  |
| submetaobjectname | character varying(255) | Yes |  |
- **PK:** id

### offlinecreatedguids
- **Table:** `system$offlinecreatedguids` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| guid | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
- **PK:** id

### offlinesynchronizationhistory
- **Table:** `system$offlinesynchronizationhistory` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| syncid | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
- **PK:** id

### privatefiledocument
- **Table:** `system$privatefiledocument` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### scheduledeventinformation_xasinstance
- **Table:** `system$scheduledeventinformation_xasinstance` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$scheduledeventinformationid | bigint | No |  |
| system$xasinstanceid | bigint | No |  |
- **PK:** system$scheduledeventinformationid, system$xasinstanceid

### synchronizationerror
- **Table:** `system$synchronizationerror` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| reason | text | Yes |  |
| objectid | character varying(200) | Yes |  |
| objecttype | character varying(1000) | Yes |  |
| objectcontent | text | Yes |  |
| createddate | timestamp without time zone | Yes |  |
| system$owner | bigint | Yes |  |
- **PK:** id

### synchronizationerrorfile
- **Table:** `system$synchronizationerrorfile` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### synchronizationerrorfile_synchronizationerror
- **Table:** `system$synchronizationerrorfile_synchronizationerror` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$synchronizationerrorfileid | bigint | No |  |
| system$synchronizationerrorid | bigint | No |  |
- **PK:** system$synchronizationerrorfileid, system$synchronizationerrorid

### thumbnail
- **Table:** `system$thumbnail` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
- **PK:** id

### thumbnail_image
- **Table:** `system$thumbnail_image` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$thumbnailid | bigint | No |  |
| system$imageid | bigint | No |  |
- **PK:** system$thumbnailid, system$imageid

### tokeninformation
- **Table:** `system$tokeninformation` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| token | character varying(200) | Yes |  |
| expirydate | timestamp without time zone | Yes |  |
| useragent | text | Yes |  |
- **PK:** id

### tokeninformation_user
- **Table:** `system$tokeninformation_user` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$tokeninformationid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** system$tokeninformationid, system$userid

### unreferencedfile
- **Table:** `system$unreferencedfile` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| filekey | character varying(36) | Yes |  |
| state | character varying(8) | Yes |  |
| transactionid | character varying(36) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
- **PK:** id

### unreferencedfile_xasinstance
- **Table:** `system$unreferencedfile_xasinstance` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$unreferencedfileid | bigint | No |  |
| system$xasinstanceid | bigint | No |  |
- **PK:** system$unreferencedfileid, system$xasinstanceid

### userreportinfo
- **Table:** `system$userreportinfo` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| usertype | character varying(8) | Yes |  |
| hash | character varying(64) | Yes |  |
- **PK:** id

### userreportinfo_user
- **Table:** `system$userreportinfo_user` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$userreportinfoid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** system$userreportinfoid, system$userid

### workflow
- **Table:** `system$workflow` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | character varying(200) | Yes |  |
| description | text | Yes |  |
| starttime | timestamp without time zone | Yes |  |
| endtime | timestamp without time zone | Yes |  |
| duedate | timestamp without time zone | Yes |  |
| canberestarted | boolean | Yes |  |
| canbecontinued | boolean | Yes |  |
| state | character varying(12) | Yes |  |
| reason | text | Yes |  |
| previousstate | character varying(12) | Yes |  |
| objectid | bigint | Yes |  |
| processingstate | character varying(30) | Yes |  |
| system$owner | bigint | Yes |  |
| canapplyjumpto | boolean | Yes |  |
- **PK:** id

### workflow_currentactivity
- **Table:** `system$workflow_currentactivity` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowid | bigint | No |  |
| system$workflowactivityid | bigint | No |  |
- **PK:** system$workflowid, system$workflowactivityid

### workflow_parentworkflow
- **Table:** `system$workflow_parentworkflow` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowid1 | bigint | No |  |
| system$workflowid2 | bigint | No |  |
- **PK:** system$workflowid1, system$workflowid2

### workflow_workflowdefinition
- **Table:** `system$workflow_workflowdefinition` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowid | bigint | No |  |
| system$workflowdefinitionid | bigint | No |  |
- **PK:** system$workflowid, system$workflowdefinitionid

### workflowactivity
- **Table:** `system$workflowactivity` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| modelguid | character varying(36) | Yes |  |
| activityguid | character varying(36) | Yes |  |
| caption | text | Yes |  |
| detailsjson | text | Yes |  |
| state | character varying(9) | Yes |  |
| starttime | timestamp without time zone | Yes |  |
| endtime | timestamp without time zone | Yes |  |
| actiontime | timestamp without time zone | Yes |  |
| reason | text | Yes |  |
| activityhash | character varying(200) | Yes |  |
| isderivedactivity | boolean | Yes |  |
| outcome | character varying(200) | Yes |  |
| outcomemodelguid | character varying(36) | Yes |  |
- **PK:** id

### workflowactivity_actor
- **Table:** `system$workflowactivity_actor` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** system$workflowactivityid, system$userid

### workflowactivity_previousactivity
- **Table:** `system$workflowactivity_previousactivity` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityid1 | bigint | No |  |
| system$workflowactivityid2 | bigint | No |  |
- **PK:** system$workflowactivityid1, system$workflowactivityid2

### workflowactivity_subworkflow
- **Table:** `system$workflowactivity_subworkflow` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityid | bigint | No |  |
| system$workflowid | bigint | No |  |
- **PK:** system$workflowactivityid, system$workflowid

### workflowactivity_workflow
- **Table:** `system$workflowactivity_workflow` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityid | bigint | No |  |
| system$workflowid | bigint | No |  |
- **PK:** system$workflowactivityid, system$workflowid

### workflowactivity_workflowusertask
- **Table:** `system$workflowactivity_workflowusertask` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityid | bigint | No |  |
| system$workflowusertaskid | bigint | No |  |
- **PK:** system$workflowactivityid, system$workflowusertaskid

### workflowactivity_workflowversion
- **Table:** `system$workflowactivity_workflowversion` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityid | bigint | No |  |
| system$workflowversionid | bigint | No |  |
- **PK:** system$workflowactivityid, system$workflowversionid

### workflowactivityusertaskoutcome
- **Table:** `system$workflowactivityusertaskoutcome` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| outcome | character varying(200) | Yes |  |
| time | timestamp without time zone | Yes |  |
- **PK:** id

### workflowactivityusertaskoutcome_user
- **Table:** `system$workflowactivityusertaskoutcome_user` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityusertaskoutcomeid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** system$workflowactivityusertaskoutcomeid, system$userid

### workflowactivityusertaskoutcome_workflowactivity
- **Table:** `system$workflowactivityusertaskoutcome_workflowactivity` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowactivityusertaskoutcomeid | bigint | No |  |
| system$workflowactivityid | bigint | No |  |
- **PK:** system$workflowactivityusertaskoutcomeid, system$workflowactivityid

### workflowdefinition
- **Table:** `system$workflowdefinition` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | character varying(200) | Yes |  |
| title | character varying(200) | Yes |  |
| isobsolete | boolean | Yes |  |
| islocked | boolean | Yes |  |
| modelguid | character varying(36) | Yes |  |
- **PK:** id

### workflowdefinition_currentworkflowversion
- **Table:** `system$workflowdefinition_currentworkflowversion` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowdefinitionid | bigint | No |  |
| system$workflowversionid | bigint | No |  |
- **PK:** system$workflowdefinitionid, system$workflowversionid

### workflowusertask
- **Table:** `system$workflowusertask` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | text | Yes |  |
| description | text | Yes |  |
| starttime | timestamp without time zone | Yes |  |
| duedate | timestamp without time zone | Yes |  |
| endtime | timestamp without time zone | Yes |  |
| outcome | character varying(200) | Yes |  |
| state | character varying(10) | Yes |  |
| completiontype | character varying(9) | Yes |  |
| processingstate | character varying(30) | Yes |  |
| error | text | Yes |  |
- **PK:** id

### workflowusertask_assignees
- **Table:** `system$workflowusertask_assignees` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowusertaskid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** system$workflowusertaskid, system$userid

### workflowusertask_targetusers
- **Table:** `system$workflowusertask_targetusers` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowusertaskid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** system$workflowusertaskid, system$userid

### workflowusertask_workflow
- **Table:** `system$workflowusertask_workflow` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowusertaskid | bigint | No |  |
| system$workflowid | bigint | No |  |
- **PK:** system$workflowusertaskid, system$workflowid

### workflowusertask_workflowusertaskdefinition
- **Table:** `system$workflowusertask_workflowusertaskdefinition` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowusertaskid | bigint | No |  |
| system$workflowusertaskdefinitionid | bigint | No |  |
- **PK:** system$workflowusertaskid, system$workflowusertaskdefinitionid

### workflowusertaskdefinition
- **Table:** `system$workflowusertaskdefinition` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| name | character varying(200) | Yes |  |
| isobsolete | boolean | Yes |  |
| modelguid | character varying(36) | Yes |  |
- **PK:** id

### workflowusertaskdefinition_workflowdefinition
- **Table:** `system$workflowusertaskdefinition_workflowdefinition` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowusertaskdefinitionid | bigint | No |  |
| system$workflowdefinitionid | bigint | No |  |
- **PK:** system$workflowusertaskdefinitionid, system$workflowdefinitionid

### workflowusertaskoutcome
- **Table:** `system$workflowusertaskoutcome` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| outcome | character varying(200) | Yes |  |
| time | timestamp without time zone | Yes |  |
- **PK:** id

### workflowusertaskoutcome_user
- **Table:** `system$workflowusertaskoutcome_user` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowusertaskoutcomeid | bigint | No |  |
| system$userid | bigint | No |  |
- **PK:** system$workflowusertaskoutcomeid, system$userid

### workflowusertaskoutcome_workflowusertask
- **Table:** `system$workflowusertaskoutcome_workflowusertask` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowusertaskoutcomeid | bigint | No |  |
| system$workflowusertaskid | bigint | No |  |
- **PK:** system$workflowusertaskoutcomeid, system$workflowusertaskid

### workflowversion
- **Table:** `system$workflowversion` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| versionhash | character varying(200) | Yes |  |
| modeljson | text | Yes |  |
- **PK:** id

### workflowversion_previousversion
- **Table:** `system$workflowversion_previousversion` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowversionid1 | bigint | No |  |
| system$workflowversionid2 | bigint | No |  |
- **PK:** system$workflowversionid1, system$workflowversionid2

### workflowversion_workflowdefinition
- **Table:** `system$workflowversion_workflowdefinition` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowversionid | bigint | No |  |
| system$workflowdefinitionid | bigint | No |  |
- **PK:** system$workflowversionid, system$workflowdefinitionid

### workflowversion_workflowusertaskdefinition
- **Table:** `system$workflowversion_workflowusertaskdefinition` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| system$workflowversionid | bigint | No |  |
| system$workflowusertaskdefinitionid | bigint | No |  |
- **PK:** system$workflowversionid, system$workflowusertaskdefinitionid

### xasinstance
- **Table:** `system$xasinstance` | **Rows:** 0
| Column | Type | Nullable | Sample Values |
|--------|------|----------|---------------|
| id | bigint | No |  |
| xasid | character varying(50) | Yes |  |
| lastupdate | timestamp without time zone | Yes |  |
| allowednumberofconcurrentusers | integer | Yes |  |
| partnername | character varying(200) | Yes |  |
| customername | character varying(200) | Yes |  |
| createddate | timestamp without time zone | Yes |  |
- **PK:** id

---
