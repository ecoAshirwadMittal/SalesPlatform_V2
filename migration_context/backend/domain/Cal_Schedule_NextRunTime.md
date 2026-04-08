# Microflow Detailed Specification: Cal_Schedule_NextRunTime

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Schedule/IntervalType!=empty and $Schedule/Interval!=empty and $Schedule/Interval>=1`
   ➔ **If [true]:**
      1. **Create Variable **$NextRunTime** = `if $Schedule/NextRunTime!=empty then $Schedule/NextRunTime else [%CurrentDateTime%]`**
      2. 🔀 **DECISION:** `($NextRunTime!=empty and $NextRunTime>[%CurrentDateTime%]) or $NextRunTime=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$NextRunTime`
         ➔ **If [false]:**
            1. **Create Variable **$Diff** = `if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Minute then minutesBetween([%CurrentDateTime%],$NextRunTime) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Hour then hoursBetween([%CurrentDateTime%],$NextRunTime) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Day then daysBetween([%CurrentDateTime%],$NextRunTime) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Week then weeksBetween([%CurrentDateTime%],$NextRunTime) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Month then calendarMonthsBetween([%CurrentDateTime%],$NextRunTime)+1 else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Year then calendarYearsBetween([%CurrentDateTime%],$NextRunTime)+1 else 0`**
            2. **Create Variable **$Increase** = `ceil($Diff : $Schedule/Interval) * $Schedule/Interval`**
            3. **Update Variable **$NextRunTime** = `if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Minute then addMinutes($NextRunTime,$Increase) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Hour then addHours($NextRunTime,$Increase) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Day then addDays($NextRunTime,$Increase) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Week then addWeeks($NextRunTime,$Increase) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Month then addMonths($NextRunTime,$Increase) else if $Schedule/IntervalType = TaskQueueScheduler.Enum_IntervalType.Year then addYears($NextRunTime, $Increase) else empty`**
            4. 🏁 **END:** Return `$NextRunTime`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [DateTime] value.