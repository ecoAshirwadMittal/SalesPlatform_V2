# Starts slack-claude-bot in the background, writes PID + logs.
# Idempotent: exits 0 if already running.

$ErrorActionPreference = 'Stop'
$BotDir    = Split-Path -Parent $MyInvocation.MyCommand.Path
$PidFile   = Join-Path $BotDir 'bot.pid'
$LogFile   = Join-Path $BotDir 'bot.log'
$ErrFile   = Join-Path $BotDir 'bot.err.log'
$EnvFile   = Join-Path $BotDir '.env'
$CrashFile = Join-Path $BotDir 'bot.crash'

function Test-BotRunning {
    if (-not (Test-Path $PidFile)) { return $false }
    $processId = Get-Content $PidFile -ErrorAction SilentlyContinue
    if (-not $processId) { return $false }
    $proc = Get-Process -Id $processId -ErrorAction SilentlyContinue
    if (-not $proc) { return $false }
    # Confirm it's actually our node process, not a recycled PID
    if ($proc.ProcessName -ne 'node') { return $false }
    return $true
}

if (Test-BotRunning) {
    Write-Host "[$(Get-Date -Format s)] bot already running (PID $(Get-Content $PidFile))"
    # Reset crash counter on healthy run
    if (Test-Path $CrashFile) { Remove-Item $CrashFile -Force }
    exit 0
}

# Hard precondition: .env must exist, otherwise we crash-loop forever
if (-not (Test-Path $EnvFile)) {
    Write-Host "[$(Get-Date -Format s)] SKIP: .env missing - create it from .env.example before the bot can start"
    exit 0
}

# Crash-loop guard: if bot died within 60s of its last start, back off.
# After 3 consecutive fast-fails, stop trying until .crash is cleared.
$crashCount = 0
if (Test-Path $CrashFile) {
    $crashCount = [int](Get-Content $CrashFile -ErrorAction SilentlyContinue)
}
if ($crashCount -ge 3) {
    Write-Host "[$(Get-Date -Format s)] SKIP: bot is crash-looping after $crashCount fast-fails. Fix the error, then: Remove-Item bot.crash"
    if (Test-Path $ErrFile) {
        Write-Host '--- last stderr ---'
        Get-Content $ErrFile -Tail 10
    }
    exit 0
}

# Ensure deps installed
if (-not (Test-Path (Join-Path $BotDir 'node_modules'))) {
    Write-Host "[$(Get-Date -Format s)] installing npm deps..."
    Push-Location $BotDir
    try { & npm install --silent } finally { Pop-Location }
}

Write-Host "[$(Get-Date -Format s)] starting slack-claude-bot..."
$proc = Start-Process -FilePath 'node' `
    -ArgumentList 'index.js' `
    -WorkingDirectory $BotDir `
    -WindowStyle Hidden `
    -RedirectStandardOutput $LogFile `
    -RedirectStandardError  $ErrFile `
    -PassThru

$proc.Id | Out-File -FilePath $PidFile -Encoding ascii
Write-Host "[$(Get-Date -Format s)] started PID $($proc.Id)"

# Wait 5s and verify the process is still alive. If not, increment crash counter.
Start-Sleep -Seconds 5
$still = Get-Process -Id $proc.Id -ErrorAction SilentlyContinue
if (-not $still) {
    $crashCount++
    $crashCount | Out-File -FilePath $CrashFile -Encoding ascii
    Write-Host "[$(Get-Date -Format s)] bot died within 5s, fast-fail number $crashCount"
    if (Test-Path $ErrFile) {
        Write-Host '--- stderr ---'
        Get-Content $ErrFile -Tail 10
    }
    exit 1
}
# Healthy start - clear crash counter
if (Test-Path $CrashFile) { Remove-Item $CrashFile -Force }
