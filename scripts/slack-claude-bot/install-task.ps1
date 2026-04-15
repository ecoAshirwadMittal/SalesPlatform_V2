# Registers the Windows Scheduled Task that runs watchdog.ps1 every 15 minutes.
# Run this ONCE from an elevated PowerShell (Run as Administrator).
#
# Usage:
#   powershell -ExecutionPolicy Bypass -File install-task.ps1
#
# To remove:
#   Unregister-ScheduledTask -TaskName 'SlackClaudeBotWatchdog' -Confirm:$false

$ErrorActionPreference = 'Stop'
$TaskName   = 'SlackClaudeBotWatchdog'
$BotDir     = Split-Path -Parent $MyInvocation.MyCommand.Path
$Watchdog   = Join-Path $BotDir 'watchdog.ps1'

if (-not (Test-Path $Watchdog)) {
    throw "watchdog.ps1 not found at $Watchdog"
}

# Remove existing task if present (idempotent reinstall)
if (Get-ScheduledTask -TaskName $TaskName -ErrorAction SilentlyContinue) {
    Write-Host "Removing existing task $TaskName..."
    Unregister-ScheduledTask -TaskName $TaskName -Confirm:$false
}

$action = New-ScheduledTaskAction `
    -Execute 'powershell.exe' `
    -Argument "-NoProfile -WindowStyle Hidden -ExecutionPolicy Bypass -File `"$Watchdog`""

# Trigger 1: at logon (starts bot when you sign in)
$triggerLogon = New-ScheduledTaskTrigger -AtLogOn -User $env:USERNAME

# Trigger 2: every 15 minutes, indefinitely (watchdog re-check)
$triggerRepeat = New-ScheduledTaskTrigger -Once -At (Get-Date).AddMinutes(1) `
    -RepetitionInterval (New-TimeSpan -Minutes 15) `
    -RepetitionDuration ([TimeSpan]::FromDays(3650))

$settings = New-ScheduledTaskSettingsSet `
    -AllowStartIfOnBatteries `
    -DontStopIfGoingOnBatteries `
    -StartWhenAvailable `
    -MultipleInstances IgnoreNew `
    -ExecutionTimeLimit (New-TimeSpan -Minutes 10)

$principal = New-ScheduledTaskPrincipal `
    -UserId $env:USERNAME `
    -LogonType Interactive `
    -RunLevel Limited

Register-ScheduledTask `
    -TaskName $TaskName `
    -Action $action `
    -Trigger @($triggerLogon, $triggerRepeat) `
    -Settings $settings `
    -Principal $principal `
    -Description 'Keeps slack-claude-bot running; checks every 15 minutes.' | Out-Null

Write-Host ""
Write-Host "Installed scheduled task: $TaskName"
Write-Host "  - Runs at logon"
Write-Host "  - Re-checks every 15 minutes"
Write-Host "  - Logs to: $(Join-Path $BotDir 'watchdog.log')"
Write-Host ""
Write-Host "Kick off the first run now with:"
Write-Host "  Start-ScheduledTask -TaskName $TaskName"
