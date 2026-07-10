param(
    [string]$BaseUrl = "http://localhost:8080",
    [int]$AppProcessId,
    [int]$DurationMinutes = 30,
    [int]$IntervalMinutes = 5,
    [int]$RequestsPerMinute = 12,
    [string]$OutputCsv = "docs/monitoreo/linea-base.csv"
)

$ErrorActionPreference = "Stop"

function Get-ActuatorMetric {
    param(
        [string]$Metric,
        [string[]]$Tags = @()
    )

    $query = ""
    if ($Tags.Count -gt 0) {
        $query = "?" + (($Tags | ForEach-Object { "tag=$_" }) -join "&")
    }

    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/metrics/$Metric$query" -TimeoutSec 10
    } catch {
        return $null
    }
    $measure = $response.measurements | Select-Object -First 1
    if ($null -eq $measure) {
        return $null
    }
    return [double]$measure.value
}

function Get-HttpMetric {
    param(
        [string]$Statistic
    )

    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/metrics/http.server.requests?tag=uri:/login" -TimeoutSec 10
    } catch {
        return $null
    }
    $measure = $response.measurements | Where-Object { $_.statistic -eq $Statistic } | Select-Object -First 1
    if ($null -eq $measure) {
        return $null
    }
    return [double]$measure.value
}

function Get-LoginHistogramBuckets {
    $content = (Invoke-WebRequest -Uri "$BaseUrl/actuator/prometheus" -UseBasicParsing -TimeoutSec 10).Content
    $buckets = @{}

    foreach ($line in ($content -split "`n")) {
        if ($line -match 'http_server_requests_seconds_bucket\{.*uri="/login".*le="([^"]+)".*\}\s+([0-9.Ee+-]+)') {
            $buckets[$matches[1]] = [double]::Parse($matches[2], [Globalization.CultureInfo]::InvariantCulture)
        }
    }

    return $buckets
}

function Get-HistogramP95Milliseconds {
    param(
        [hashtable]$InitialBuckets,
        [hashtable]$CurrentBuckets
    )

    if ($null -eq $CurrentBuckets -or -not $CurrentBuckets.ContainsKey("+Inf")) {
        return $null
    }

    $total = $CurrentBuckets["+Inf"]
    if ($null -ne $InitialBuckets -and $InitialBuckets.ContainsKey("+Inf")) {
        $total = $total - $InitialBuckets["+Inf"]
    }

    if ($total -le 0) {
        return $null
    }

    $target = $total * 0.95
    $finiteBuckets = $CurrentBuckets.Keys |
        Where-Object { $_ -ne "+Inf" } |
        Sort-Object { [double]::Parse($_, [Globalization.CultureInfo]::InvariantCulture) }

    foreach ($upperBound in $finiteBuckets) {
        $bucketCount = $CurrentBuckets[$upperBound]
        if ($null -ne $InitialBuckets -and $InitialBuckets.ContainsKey($upperBound)) {
            $bucketCount = $bucketCount - $InitialBuckets[$upperBound]
        }

        if ($bucketCount -ge $target) {
            return [double]::Parse($upperBound, [Globalization.CultureInfo]::InvariantCulture) * 1000
        }
    }

    return $null
}

function Format-Nullable {
    param($Value)

    if ($null -eq $Value) {
        return "no medida"
    }
    return ([double]$Value).ToString("0.######", [Globalization.CultureInfo]::InvariantCulture)
}

if (-not (Test-Path "docs/monitoreo")) {
    New-Item -ItemType Directory -Path "docs/monitoreo" | Out-Null
}

$periodSeconds = [math]::Max(1, [int](60 / $RequestsPerMinute))
$totalSamples = [int]($DurationMinutes / $IntervalMinutes) + 1
$rows = New-Object System.Collections.Generic.List[object]

$loginWarmup = Invoke-WebRequest -Uri "$BaseUrl/login" -UseBasicParsing -TimeoutSec 10
if ($loginWarmup.StatusCode -ge 500) {
    throw "La prueba controlada no inicia porque /login respondio $($loginWarmup.StatusCode)"
}

$initialCount = Get-HttpMetric "COUNT"
$initialTotalTime = Get-HttpMetric "TOTAL_TIME"
$initialErrors = Get-ActuatorMetric "http.server.requests" @("uri:/login", "status:500")
$initialBuckets = Get-LoginHistogramBuckets

if ($null -eq $initialCount) { $initialCount = 0 }
if ($null -eq $initialTotalTime) { $initialTotalTime = 0 }
if ($null -eq $initialErrors) { $initialErrors = 0 }

$start = Get-Date
$nextSample = $start
$sampleIndex = 0

while ($sampleIndex -lt $totalSamples) {
    $now = Get-Date

    if ($now -ge $nextSample -and $sampleIndex -lt $totalSamples) {
        $process = $null
        if ($AppProcessId -gt 0) {
            $process = Get-Process -Id $AppProcessId -ErrorAction SilentlyContinue
        }

        $count = Get-HttpMetric "COUNT"
        $totalTime = Get-HttpMetric "TOTAL_TIME"
        $p95 = Get-HistogramP95Milliseconds -InitialBuckets $initialBuckets -CurrentBuckets (Get-LoginHistogramBuckets)
        $errors = Get-ActuatorMetric "http.server.requests" @("uri:/login", "status:500")
        $heapBytes = Get-ActuatorMetric "jvm.memory.used" @("area:heap")
        $diskFree = Get-ActuatorMetric "disk.free"
        $diskTotal = Get-ActuatorMetric "disk.total"
        $cpuProcess = Get-ActuatorMetric "process.cpu.usage"

        if ($null -eq $count) { $count = 0 }
        if ($null -eq $totalTime) { $totalTime = 0 }
        if ($null -eq $errors) { $errors = 0 }

        $deltaCount = $count - $initialCount
        $deltaTime = $totalTime - $initialTotalTime
        $deltaErrors = $errors - $initialErrors
        $elapsedMinutes = [math]::Max(1.0 / 60.0, (($now - $start).TotalMinutes))

        $avgMs = $null
        if ($deltaCount -gt 0) {
            $avgMs = ($deltaTime / $deltaCount) * 1000
        }

        $rpm = $deltaCount / $elapsedMinutes
        $errorRate = $null
        if ($deltaCount -gt 0) {
            $errorRate = ($deltaErrors / $deltaCount) * 100
        }

        $diskUsedBytes = $null
        if ($null -ne $diskFree -and $null -ne $diskTotal) {
            $diskUsedBytes = $diskTotal - $diskFree
        }

        $rows.Add([pscustomobject]@{
            timestamp = $now.ToString("yyyy-MM-dd HH:mm:ss")
            elapsed_minutes = [math]::Round(($now - $start).TotalMinutes, 2)
            cpu_process_percent = Format-Nullable $(if ($null -ne $cpuProcess) { $cpuProcess * 100 } else { $null })
            ram_process_mb = Format-Nullable $(if ($null -ne $process) { $process.WorkingSet64 / 1MB } else { $null })
            jvm_heap_mb = Format-Nullable $(if ($null -ne $heapBytes) { $heapBytes / 1MB } else { $null })
            avg_response_ms_login = Format-Nullable $avgMs
            p95_latency_ms_login = Format-Nullable $p95
            requests_per_minute_login = Format-Nullable $rpm
            http_5xx_error_rate_percent_login = Format-Nullable $errorRate
            disk_used_mb = Format-Nullable $(if ($null -ne $diskUsedBytes) { $diskUsedBytes / 1MB } else { $null })
            notes = $(if ($AppProcessId -gt 0 -and $null -eq $process) { "RAM no medida: proceso no encontrado" } else { "" })
        }) | Out-Null

        $nextSample = $nextSample.AddMinutes($IntervalMinutes)
        $sampleIndex++
    }

    if ($sampleIndex -lt $totalSamples) {
        try {
            $result = Invoke-WebRequest -Uri "$BaseUrl/login" -UseBasicParsing -TimeoutSec 10
            if ($result.StatusCode -ge 500) {
                Write-Warning "/login respondio $($result.StatusCode)"
            }
        } catch {
            Write-Warning "Error solicitando /login: $($_.Exception.Message)"
        }

        Start-Sleep -Seconds $periodSeconds
    }
}

$rows | Export-Csv -Path $OutputCsv -NoTypeInformation -Encoding UTF8
$rows | Format-Table -AutoSize
