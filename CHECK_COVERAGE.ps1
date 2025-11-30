# PowerShell script to run tests and display coverage percentage

Write-Host "Running tests with coverage..." -ForegroundColor Cyan
Write-Host ""

# Run Maven tests with JaCoCo
mvn clean test

# Check if the XML report exists
$xmlReportPath = "target\site\jacoco\jacoco.xml"

if (Test-Path $xmlReportPath) {
    Write-Host ""
    Write-Host "===========================================" -ForegroundColor Green
    Write-Host "COVERAGE REPORT" -ForegroundColor Green
    Write-Host "===========================================" -ForegroundColor Green
    Write-Host ""
    
    # Load and parse the XML report
    [xml]$coverageReport = Get-Content $xmlReportPath
    
    # Get the report element which contains coverage data
    $report = $coverageReport.report
    
    # Calculate coverage percentages from the counter elements
    $totalInstructions = 0
    $coveredInstructions = 0
    $totalBranches = 0
    $coveredBranches = 0
    $totalLines = 0
    $coveredLines = 0
    $totalMethods = 0
    $coveredMethods = 0
    $totalClasses = 0
    $coveredClasses = 0
    
    # Function to process counters recursively
    function Process-Counters($element) {
        foreach ($counter in $element.counter) {
            switch ($counter.type) {
                "INSTRUCTION" {
                    $script:totalInstructions += [long]$counter.missed + [long]$counter.covered
                    $script:coveredInstructions += [long]$counter.covered
                }
                "BRANCH" {
                    $script:totalBranches += [long]$counter.missed + [long]$counter.covered
                    $script:coveredBranches += [long]$counter.covered
                }
                "LINE" {
                    $script:totalLines += [long]$counter.missed + [long]$counter.covered
                    $script:coveredLines += [long]$counter.covered
                }
                "METHOD" {
                    $script:totalMethods += [long]$counter.missed + [long]$counter.covered
                    $script:coveredMethods += [long]$counter.covered
                }
                "CLASS" {
                    $script:totalClasses += [long]$counter.missed + [long]$counter.covered
                    $script:coveredClasses += [long]$counter.covered
                }
            }
        }
        
        # Process child packages
        foreach ($package in $element.package) {
            Process-Counters $package
        }
    }
    
    # Process the report
    Process-Counters $report
    
    # Calculate percentages
    $instructionCoverage = if ($totalInstructions -gt 0) { [math]::Round(($coveredInstructions / $totalInstructions) * 100, 2) } else { 0 }
    $branchCoverage = if ($totalBranches -gt 0) { [math]::Round(($coveredBranches / $totalBranches) * 100, 2) } else { 0 }
    $lineCoverage = if ($totalLines -gt 0) { [math]::Round(($coveredLines / $totalLines) * 100, 2) } else { 0 }
    $methodCoverage = if ($totalMethods -gt 0) { [math]::Round(($coveredMethods / $totalMethods) * 100, 2) } else { 0 }
    $classCoverage = if ($totalClasses -gt 0) { [math]::Round(($coveredClasses / $totalClasses) * 100, 2) } else { 0 }
    
    # Display results
    Write-Host "Instruction Coverage: $instructionCoverage%" -ForegroundColor $(if ($instructionCoverage -ge 80) { "Green" } else { "Yellow" })
    Write-Host "Branch Coverage:     $branchCoverage%" -ForegroundColor $(if ($branchCoverage -ge 80) { "Green" } else { "Yellow" })
    Write-Host "Line Coverage:       $lineCoverage%" -ForegroundColor $(if ($lineCoverage -ge 80) { "Green" } else { "Yellow" })
    Write-Host "Method Coverage:     $methodCoverage%" -ForegroundColor $(if ($methodCoverage -ge 80) { "Green" } else { "Yellow" })
    Write-Host "Class Coverage:      $classCoverage%" -ForegroundColor $(if ($classCoverage -ge 80) { "Green" } else { "Yellow" })
    Write-Host ""
    
    # Check if overall coverage (instruction coverage) is above 80%
    Write-Host "===========================================" -ForegroundColor Green
    if ($instructionCoverage -ge 80) {
        Write-Host "[PASS] Coverage is above 80%!" -ForegroundColor Green
    } else {
        Write-Host "[FAIL] Coverage is below 80% ($instructionCoverage percent)" -ForegroundColor Red
    }
    Write-Host "===========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Full report available at: target\site\jacoco\index.html" -ForegroundColor Cyan
    
} else {
    Write-Host ""
    Write-Host "ERROR: Coverage report not found at $xmlReportPath" -ForegroundColor Red
    Write-Host "Make sure tests ran successfully." -ForegroundColor Yellow
}

