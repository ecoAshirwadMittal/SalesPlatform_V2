# Watchdog: checks whether slack-claude-bot is alive, restarts if not.
# Called every 15 minutes by Task Scheduler. Appends to watchdog.log.

$ErrorActionPreference = 'Stop'
$BotDir    = Split-Path -Parent $MyInvocation.MyCommand.Path
$WatchLog  = Join-Path $BotDir 'watchdog.log'
$StartBot  = Join-Path $BotDir 'start-bot.ps1'

function Write-Log($msg) {
    "$(Get-Date -Format s)  $msg" | Out-File -FilePath $WatchLog -Append -Encoding ascii
}

try {
    $output = & powershell -NoProfile -ExecutionPolicy Bypass -File $StartBot 2>&1
    Write-Log ($output -join ' | ')
} catch {
    Write-Log "ERROR: $($_.Exception.Message)"
    exit 1
}
