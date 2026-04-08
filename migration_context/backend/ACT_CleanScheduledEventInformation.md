# Microflow Detailed Specification: ACT_CleanScheduledEventInformation

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimePeriod** = `addDays( [%BeginOfCurrentDay%], -3 )`**
2. **DB Retrieve **System.ScheduledEventInformation** Filter: `[EndTime<$TimePeriod]` (Result: **$ScheduledEventInformationList**)**
3. **AggregateList**
4. **Delete**
5. **LogMessage**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.