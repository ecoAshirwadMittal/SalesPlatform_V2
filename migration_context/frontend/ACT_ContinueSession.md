# Nanoflow: ACT_ContinueSession

**Allowed Roles:** EcoATM_Direct_Theme.Administrator, EcoATM_Direct_Theme.User

## 📥 Inputs

- **$TimerHelper** (EcoATM_Direct_Theme.TimerHelper)
- **$IdleTimeout** (EcoATM_Direct_Theme.IdleTimeout)

## ⚙️ Execution Flow

1. **Close current page/popup**
2. **Delete **$TimerHelper** from Database**
3. **Update **$IdleTimeout** (and Save to DB)
      - Set **IdleTimeExtension** = `[%CurrentDateTime%]`**
4. **Call JS Action **EcoATM_Direct_Theme.JSA_ClickElement** (Result: **$Variable**)**
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
