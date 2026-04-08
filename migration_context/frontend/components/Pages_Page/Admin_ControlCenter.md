# Page: Admin_ControlCenter

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [Click] → **Page**: `OQL.Example_Overview`
  ↳ [Click] → **Page**: `MxModelReflection.MxObjects_Overview`
  ↳ [Click] → **Page**: `TaskQueueScheduler.Schedule_Overview`
  ↳ [Click] → **Page**: `TaskQueueHelpers.MonitoringDashboard`
  ↳ [Click] → **Page**: `XLSReport.Excel_Document_Overview`
  ↳ [Click] → **Page**: `XLSReport.Excel_Document_Overview`
  ↳ [Click] → **Page**: `AuctionUI.IdleTimeoutConfiguration`
  ↳ [Click] → **Page**: `Administration.Account_Overview`
  ↳ [Click] → **Page**: `EcoATM_UnitTest.UnitTest_Overview`
  ↳ [Click] → **Page**: `DeepLink.DeepLinkConfig`
  ↳ [Click] → **Page**: `ForgotPassword.ForgotPasswordConfiguration_Edit`
  ↳ [Click] → **Page**: `EcoATM_PWS.PWSUserPersonalization_Overview`
  ↳ [Click] → **Page**: `AuctionUI.MicrosoftGraph_Overview`
  ↳ [Click] → **Page**: `AuctionUI.Sharepoint_Overview`
  ↳ [Click] → **Microflow**: `SAML20.OpenConfiguration`
  ↳ [Click] → **Microflow**: `Email_Connector.ACT_EmailAccount_LaunchEmailConnectorOverview`
  ↳ [Click] → **Page**: `AuctionUI.EcoATMDirectUser_Admin_Overview`
  ↳ [Click] → **Nanoflow**: `AuctionUI.ACT_ConfirmBackupRestoreActivity`
  ↳ [Click] → **Page**: `Administration.ActiveSessions`
  ↳ [Click] → **Page**: `Administration.RuntimeInstances`
  ↳ [Click] → **Page**: `Administration.ScheduledEvents`
  ↳ [Click] → **Microflow**: `EcoATM_UserManagement.ACT_CreateTestUsers`
  ↳ [Click] → **Page**: `AuctionUI.DocumentGeneration`
  ↳ [Click] → **Page**: `EcoATM_Lock.Lock_Overview`
  ↳ [Click] → **Nanoflow**: `EcoATM_PWSMDM.NAN_Device_PropertiesUtility`
  ↳ [Click] → **Page**: `AuctionUI.PG_CarryForwardTest`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
