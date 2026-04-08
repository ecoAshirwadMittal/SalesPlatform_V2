# Snippet: SNIP_IdleTimeout

## Widget Tree

- 📦 **DataView** [MF: EcoATM_Direct_Theme.DS_GetIdleTimeoutConfig]
  - 📦 **DataView** [MF: EcoATM_Direct_Theme.DS_GetOrCreateIdleTimeout] 👁️ (If: `$currentObject != empty
and
$currentObject/IsActive`)
    - 🧩 **Timeout Alert** (ID: `ecoatm.sessionalertandtimeout.SessionAlertAndTimeout`)
        - getLastActivityTime: `'EcoATM_Direct_Theme.DS_LastUserActivity'`
        - setLastActivityTime: `'EcoATM_Direct_Theme.SUB_SetLastUserActivity'`
        - timeoutDuration: `$dataView2/IdleTimeoutWarning`
        - idleTimeoutAttribute: [Attr: EcoATM_Direct_Theme.IdleTimeout.LastRecordedActivity]
