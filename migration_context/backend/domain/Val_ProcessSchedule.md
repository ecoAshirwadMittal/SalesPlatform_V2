# Microflow Detailed Specification: Val_ProcessSchedule

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.Val_Schedule_MicroflowName_Required** (Result: **$Microflows_Required**)**
2. **Call Microflow **TaskQueueScheduler.Val_Schedule_TaskQueue_Required** (Result: **$TaskQueue_Required**)**
3. **Call Microflow **TaskQueueScheduler.Val_Schedule_ActiveFrom_Required** (Result: **$ActiveFrom_Required**)**
4. **Call Microflow **TaskQueueScheduler.Val_Schedule_ActiveUntil_GreaterThan_ActiveFrom** (Result: **$ActiveUntil_GreaterThan_ActiveFrom**)**
5. **Call Microflow **TaskQueueScheduler.Val_Schedule_Interval_Required** (Result: **$Interval_Required**)**
6. **Call Microflow **TaskQueueScheduler.Val_Schedule_IntervalType_Required** (Result: **$IntervalType_Required**)**
7. 🏁 **END:** Return `$ActiveFrom_Required +$ActiveUntil_GreaterThan_ActiveFrom +$Interval_Required +$IntervalType_Required +$Microflows_Required +$TaskQueue_Required`

**Final Result:** This process concludes by returning a [String] value.