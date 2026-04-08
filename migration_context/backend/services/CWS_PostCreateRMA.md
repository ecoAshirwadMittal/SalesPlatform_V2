# Microflow Detailed Specification: CWS_PostCreateRMA

### 📥 Inputs (Parameters)
- **$JSONContent** (Type: Variable)
- **$BearerToken** (Type: Variable)
- **$PWSConfiguration** (Type: EcoATM_PWSIntegration.PWSConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CreateRMA'`**
2. **Create Variable **$Description** = `'Start submit createRMA to Oracle [POST ' + $PWSConfiguration/OracleAPIPathToken+'/orders/processOrders] [ End Point :]'+$PWSConfiguration/OracleAPIPathCreateRMA+' [JSONContent:' +$JSONContent+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **RestCall**
5. **Call Microflow **EcoATM_PWSIntegration.ACT_AuditRestAPICalls****
6. **ImportXml**
7. **Update **$CreateOrderReponse**
      - Set **HTTPCode** = `$latestHttpResponse/StatusCode`
      - Set **JSONResponse** = `$CreateOrderHttpResponse`**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return `$CreateOrderReponse`

**Final Result:** This process concludes by returning a [Object] value.