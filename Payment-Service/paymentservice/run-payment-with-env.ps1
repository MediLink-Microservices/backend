param(
    [string]$EnvFile = ".env",
    [string]$JavaHome = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if (!(Test-Path -LiteralPath $EnvFile)) {
    throw "Env file not found: $EnvFile. Copy .env.example to .env and fill values."
}

Get-Content -LiteralPath $EnvFile | ForEach-Object {
    $line = $_.Trim()
    if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
        return
    }

    $pair = $line -split "=", 2
    if ($pair.Count -ne 2) {
        return
    }

    $key = $pair[0].Trim()
    $value = $pair[1].Trim()

    if ($value.StartsWith('"') -and $value.EndsWith('"')) {
        $value = $value.Substring(1, $value.Length - 2)
    }

    if ($value.StartsWith("'") -and $value.EndsWith("'")) {
        $value = $value.Substring(1, $value.Length - 2)
    }

    Set-Item -Path "Env:$key" -Value $value
}

if (Test-Path -LiteralPath $JavaHome) {
    $env:JAVA_HOME = $JavaHome
    if ($env:Path -notlike "*$JavaHome\bin*") {
        $env:Path = "$JavaHome\bin;$env:Path"
    }
}

Write-Host "Loaded .env and starting payment-service..."
Write-Host "FRONTEND_BASE_URL = $env:FRONTEND_BASE_URL"
Write-Host "JAVA_HOME = $env:JAVA_HOME"

& .\mvnw.cmd clean spring-boot:run
