# Microflow Detailed Specification: CWS_PostCreateOrder

### 📥 Inputs (Parameters)
- **$JSONContent** (Type: Variable)
- **$BaererToken** (Type: Variable)
- **$PWSConfiguration** (Type: EcoATM_PWSIntegration.PWSConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CreateOrder'`**
2. **Create Variable **$Description** = `'Start submit createOrder to Oracle [POST ' + $PWSConfiguration/OracleAPIPathToken+'/orders/processOrders] [ End Point :]'+$PWSConfiguration/OracleAPIPathCreateOrder+' [JSONContent:' +$JSONContent+']'`**
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