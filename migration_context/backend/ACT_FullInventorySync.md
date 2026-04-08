# Microflow Detailed Specification: ACT_FullInventorySync

### 📥 Inputs (Parameters)
- **$PublishReport** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [true]:**
      1. **Create Variable **$TimerName** = `'PWSInventorySync'`**
      2. **Create Variable **$Description** = `'PWS Full Inventory Sync '`**
      3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      4. **DB Retrieve **EcoATM_PWSIntegration.PWSInventory**  (Result: **$PWSInventory**)**
      5. **DB Retrieve **EcoATM_PWSIntegration.DeposcoConfig**  (Result: **$DeposcoConfig**)**
      6. **JavaCallAction**
      7. **Create Variable **$PWSDeviceCount** = `$PWSInventory/DeviceCount`**
      8. **Create Variable **$PageCount** = `if($DeposcoConfig/PageCount!=empty) then max($DeposcoConfig/PageCount,ceil($PWSInventory/DeviceCount div 1000))+1 else ceil($PWSDeviceCount div 1000) +1`**
      9. **Retrieve related **DesposcoAPIs_DeposcoConfig** via Association from **$DeposcoConfig** (Result: **$DesposcoAPIsList**)**
      10. **List Operation: **Find** on **$undefined** where `EcoATM_PWSIntegration.ENUM_DeposcoServices.Inventory` (Result: **$DesposcoAPI**)**
      11. **CreateList**
      12. **CreateList**
      13. **CreateList**
      14. **Create Variable **$SyncBeginTime** = `[%CurrentDateTime%]`**
      15. 🔄 **LOOP:** For each **$undefined** in **$undefined**
         │ 1. **Call Microflow **Custom_Logging.SUB_Log_Info****
         │ 2. **Call Microflow **EcoATM_PWSIntegration.SUB_LoadPWSInventory_Task_Deposco_Test** (Result: **$DeviceCount**)**
         │ 3. **Update Variable **$PageCount** = `$PageCount-1`**
         └─ **End Loop**
      16. **Commit/Save **$ItemInventoryItemList** to Database**
      17. **Update **$DeposcoConfig** (and Save to DB)
      - Set **LastSyncTime** = `$SyncBeginTime`**
      18. **JavaCallAction**
      19. 🔀 **DECISION:** `$PublishReport`
         ➔ **If [true]:**
            1. **Create Variable **$GenerateDeltaReportSQL** = `'UPDATE "ecoatm_pwsintegration$deposcoconfig" dc SET reportattr = sub.result_json FROM ( SELECT json_agg(row_to_json(t)) AS result_json FROM ( SELECT COALESCE(d.sku, sub2.itemnumber) AS sku, d.lastsynctime AS previoussynctime, cast(d.availableqty as integer) AS previoussyncqty, dc2.lastsynctime AS synctime, sub2.total_atp AS syncqty, (sub2.total_atp - CAST(d.availableqty AS integer)) AS deltaqty, CASE WHEN d.sku IS NULL THEN ''Sales Platform'' WHEN sub2.itemnumber IS NULL THEN ''Deposco'' ELSE ''N/A'' END AS notfound FROM "ecoatm_pwsmdm$device" d FULL OUTER JOIN ( SELECT iit.itemnumber, SUM(FLOOR(CAST(availabletopromise AS DECIMAL))) AS total_atp FROM "ecoatm_pwsintegration$inventory_test" it JOIN "ecoatm_pwsintegration$facilityinventoryitem_test" fit ON it."ecoatm_pwsintegration$inventory_test_facilityinventoryitem_test" = fit.id JOIN "ecoatm_pwsintegrati$facilityinventoryi_test_iteminventoryi_test" epftit ON epftit."ecoatm_pwsintegration$facilityinventoryitem_testid" = fit.id JOIN "ecoatm_pwsintegration$iteminventoryitem_test" iit ON iit.id = epftit.ecoatm_pwsintegration$iteminventoryitem_testid GROUP BY iit.itemnumber ) sub2 ON d.sku = sub2.itemnumber JOIN "ecoatm_pwsintegration$deposcoconfig" dc2 ON 1=1 WHERE d.sku IS NULL OR sub2.itemnumber IS null or d.availableqty is distinct from sub2.total_atp ) t ) sub '`**
            2. **JavaCallAction**
            3. **DB Retrieve **EcoATM_PWSIntegration.DeposcoConfig**  (Result: **$DeposcoConfig_Updated**)**
            4. **Create Variable **$SyncReportJSON** = `$DeposcoConfig_Updated/ReportAttr`**
            5. **ImportXml**
            6. **List Operation: **Find** on **$undefined** where `'PWS00000106992'` (Result: **$SKUSyncDetail**)**
            7. **Create **EcoATM_PWS.PWSInventorySyncReport** (Result: **$NewPWSInventorySyncReport**)
      - Set **ReportDate** = `$SyncBeginTime`
      - Set **SKUSyncDetail_PWSInventorySyncReport** = `$SyncSKUDetails`**
            8. **Create Variable **$UpdateDeltaSKUs** = `' UPDATE "ecoatm_pwsmdm$device" d SET availableqty = sub.total_atp, lastsynctime=sub.lastsynctime, deposcopageno=sub._parameter_ FROM ( SELECT iit.itemnumber, iit._parameter_,dc.lastsynctime, SUM(CAST(availabletopromise AS DECIMAL)) AS total_atp FROM "ecoatm_pwsintegration$inventory_test" it JOIN "ecoatm_pwsintegration$facilityinventoryitem_test" fit ON it."ecoatm_pwsintegration$inventory_test_facilityinventoryitem_test" = fit.id JOIN "ecoatm_pwsintegrati$facilityinventoryi_test_iteminventoryi_test" epftit ON epftit."ecoatm_pwsintegration$facilityinventoryitem_testid" = fit.id JOIN "ecoatm_pwsintegration$iteminventoryitem_test" iit ON iit.id = epftit.ecoatm_pwsintegration$iteminventoryitem_testid JOIN "ecoatm_pwsintegration$deposcoconfig" dc ON 1=1 GROUP BY iit.itemnumber,iit._parameter_,dc.lastsynctime ) sub WHERE d.sku = sub.itemnumber AND (d.availableqty IS DISTINCT FROM sub.total_atp) ; '`**
            9. **JavaCallAction**
            10. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[LastSyncTime=$SyncBeginTime]` (Result: **$DeviceWithDeltas**)**
            11. **AggregateList**
            12. **Call Microflow **Custom_Logging.SUB_Log_Info****
            13. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice****
            14. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
            15. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            16. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Create Variable **$UpdateDeltaSKUs** = `' UPDATE "ecoatm_pwsmdm$device" d SET availableqty = sub.total_atp, lastsynctime=sub.lastsynctime, deposcopageno=sub._parameter_ FROM ( SELECT iit.itemnumber, iit._parameter_,dc.lastsynctime, SUM(CAST(availabletopromise AS DECIMAL)) AS total_atp FROM "ecoatm_pwsintegration$inventory_test" it JOIN "ecoatm_pwsintegration$facilityinventoryitem_test" fit ON it."ecoatm_pwsintegration$inventory_test_facilityinventoryitem_test" = fit.id JOIN "ecoatm_pwsintegrati$facilityinventoryi_test_iteminventoryi_test" epftit ON epftit."ecoatm_pwsintegration$facilityinventoryitem_testid" = fit.id JOIN "ecoatm_pwsintegration$iteminventoryitem_test" iit ON iit.id = epftit.ecoatm_pwsintegration$iteminventoryitem_testid JOIN "ecoatm_pwsintegration$deposcoconfig" dc ON 1=1 GROUP BY iit.itemnumber,iit._parameter_,dc.lastsynctime ) sub WHERE d.sku = sub.itemnumber AND (d.availableqty IS DISTINCT FROM sub.total_atp) ; '`**
            2. **JavaCallAction**
            3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[LastSyncTime=$SyncBeginTime]` (Result: **$DeviceWithDeltas**)**
            4. **AggregateList**
            5. **Call Microflow **Custom_Logging.SUB_Log_Info****
            6. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice****
            7. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
            8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            9. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.