$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

$credentialPath = Join-Path $PSScriptRoot ".local-login"
if (-not (Test-Path $credentialPath)) {
    $randomBytes = New-Object byte[] 24
    $randomGenerator = New-Object Security.Cryptography.RNGCryptoServiceProvider
    $randomGenerator.GetBytes($randomBytes)
    $randomGenerator.Dispose()
    $password = [Convert]::ToBase64String($randomBytes).TrimEnd("=").Replace("+", "-").Replace("/", "_")
    Set-Content -Path $credentialPath -Value @("admin", $password) -Encoding Ascii
}

$credentials = Get-Content -Path $credentialPath
if ($credentials.Count -lt 2 -or [string]::IsNullOrWhiteSpace($credentials[0]) -or $credentials[1].Length -lt 12) {
    throw "The local login file is invalid. Delete webapp\.local-login and run this script again."
}

$env:HOSPITAL_WEB_USER = $credentials[0]
$env:HOSPITAL_WEB_PASSWORD = $credentials[1]
$env:SPRING_DATASOURCE_URL = "jdbc:h2:file:./data/hospital_management;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
$env:HOSPITAL_DB_USER = "sa"
$env:HOSPITAL_DB_PASSWORD = ""
$env:SERVER_ADDRESS = "127.0.0.1"

Write-Host "Local sign-in username: $env:HOSPITAL_WEB_USER"
Write-Host "Local sign-in password: $env:HOSPITAL_WEB_PASSWORD"
Write-Host "Starting the website at http://localhost:8080"
Write-Host "Leave this window open while you use the website. Press Ctrl+C to stop it."

& mvn spring-boot:run
if ($LASTEXITCODE -ne 0) {
    throw "The website stopped with exit code $LASTEXITCODE."
}
