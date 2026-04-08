# Nanoflow: DS_CreateTimerHelper

**Allowed Roles:** EcoATM_Direct_Theme.Administrator, EcoATM_Direct_Theme.User

## 📥 Inputs

- **$IdleTimeoutConfiguration** (EcoATM_Direct_Theme.IdleTimeoutConfiguration)

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_Direct_Theme.DS_LastUserActivity** (Result: **$UiHelper**)**
2. **Create Variable **$LastActivityDifference** = `floor(secondsBetween(subtractSeconds([%CurrentDateTime%], $IdleTimeoutConfiguration/IdleTimeoutWarning), $UiHelper/DateTime))`**
3. 🔀 **DECISION:** `$LastActivityDifference > $IdleTimeoutConfiguration/WarningSeconds`
   ➔ **If [true]:**
      1. **Create **EcoATM_Direct_Theme.TimerHelper** (Result: **$NewTimerHelper_Timeup**)
      - Set **Minutes** = `0`
      - Set **Seconds** = `0`
      - Set **MultiTabCheckInterval** = `$IdleTimeoutConfiguration/MultiTabCheckInterval`**
      2. 🏁 **END:** Return `$NewTimerHelper_Timeup`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$LastActivityDifference <= 0`
         ➔ **If [false]:**
            1. **Create **EcoATM_Direct_Theme.TimerHelper** (Result: **$NewTimerHelper**)
      - Set **Minutes** = `floor(($IdleTimeoutConfiguration/WarningSeconds - $LastActivityDifference) div 60)`
      - Set **Seconds** = `($IdleTimeoutConfiguration/WarningSeconds - $LastActivityDifference) mod 60`
      - Set **MultiTabCheckInterval** = `$IdleTimeoutConfiguration/MultiTabCheckInterval`**
            2. 🏁 **END:** Return `$NewTimerHelper`
         ➔ **If [true]:**
            1. **Create **EcoATM_Direct_Theme.TimerHelper** (Result: **$NewTimerHelper_Negative**)
      - Set **Minutes** = `floor($IdleTimeoutConfiguration/WarningSeconds div 60)`
      - Set **Seconds** = `$IdleTimeoutConfiguration/WarningSeconds mod 60`
      - Set **MultiTabCheckInterval** = `$IdleTimeoutConfiguration/MultiTabCheckInterval`**
            2. 🏁 **END:** Return `$NewTimerHelper_Negative`

## 🏁 Returns
`Object`
