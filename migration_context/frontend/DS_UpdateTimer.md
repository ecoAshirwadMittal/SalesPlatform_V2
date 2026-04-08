# Nanoflow: DS_UpdateTimer

**Allowed Roles:** EcoATM_Direct_Theme.Administrator, EcoATM_Direct_Theme.User

## 📥 Inputs

- **$TimerHelper** (EcoATM_Direct_Theme.TimerHelper)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$TimerHelper/Minutes <= 0 and $TimerHelper/Seconds <= 1`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$TimerHelper/Seconds = 0`
         ➔ **If [true]:**
            1. **Update **$TimerHelper**
      - Set **Seconds** = `59`
      - Set **Minutes** = `$TimerHelper/Minutes - 1`**
            2. 🔀 **DECISION:** `$TimerHelper/Seconds mod $TimerHelper/MultiTabCheckInterval = 0`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_Direct_Theme.DS_GetOrCreateIdleTimeout** (Result: **$IdleTimeout**)**
                  2. 🔀 **DECISION:** `$IdleTimeout/LastRecordedActivity != empty and $IdleTimeout/IdleTimeExtension != empty and $IdleTimeout/IdleTimeExtension >= subtractSeconds([%CurrentDateTime%], $TimerHelper/MultiTabCheckInterval)`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `true`
                     ➔ **If [true]:**
                        1. **Call Nanoflow **EcoATM_Direct_Theme.ACT_ContinueSession****
                        2. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Update **$TimerHelper**
      - Set **Seconds** = `$TimerHelper/Seconds - 1`**
            2. 🔀 **DECISION:** `$TimerHelper/Seconds mod $TimerHelper/MultiTabCheckInterval = 0`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_Direct_Theme.DS_GetOrCreateIdleTimeout** (Result: **$IdleTimeout**)**
                  2. 🔀 **DECISION:** `$IdleTimeout/LastRecordedActivity != empty and $IdleTimeout/IdleTimeExtension != empty and $IdleTimeout/IdleTimeExtension >= subtractSeconds([%CurrentDateTime%], $TimerHelper/MultiTabCheckInterval)`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `true`
                     ➔ **If [true]:**
                        1. **Call Nanoflow **EcoATM_Direct_Theme.ACT_ContinueSession****
                        2. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `true`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.SignOut** (Result: **$ReturnValueName**)**
      2. 🏁 **END:** Return `true`

## 🏁 Returns
`Boolean`
