# Nanoflow: OCE_SaveFeatureFlag

**Allowed Roles:** Eco_Core.Admin

## 📥 Inputs

- **$FeatureFlag** (Eco_Core.PWSFeatureFlag)

## ⚙️ Execution Flow

1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Commit/Save **$FeatureFlag** to Database**
3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
