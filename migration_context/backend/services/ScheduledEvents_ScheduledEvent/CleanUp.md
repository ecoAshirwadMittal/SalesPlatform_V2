# Scheduled Event: CleanUp

> This event cleans up pending deeplinks that are older than the start of the previous day, that were never executed. This prevent stale links from piling up in your database.

## Configuration

| Property | Value |
|---|---|
| **Status** | ✅ Enabled |
| **Microflow** | `DeepLink.ClearOldPendingLinks` |
| **Interval** | Every 1 Day(s) |
| **Start Time** | Tue Apr 20 2010 00:00:00 GMT+0000 (Coordinated Universal Time) |
| **Time Zone** | UTC |
