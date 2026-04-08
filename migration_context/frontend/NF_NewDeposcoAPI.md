# Nanoflow: NF_NewDeposcoAPI

**Allowed Roles:** EcoATM_PWSIntegration.Administrator

## 📥 Inputs

- **$DeposcoConfig** (EcoATM_PWSIntegration.DeposcoConfig)

## ⚙️ Execution Flow

1. **Create **EcoATM_PWSIntegration.DesposcoAPIs** (Result: **$NewDesposcoAPIs**)
      - Set **DesposcoAPIs_DeposcoConfig** = `$DeposcoConfig`**
2. **Open Page: **EcoATM_PWSIntegration.DesposcoAPIs_View****
3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
