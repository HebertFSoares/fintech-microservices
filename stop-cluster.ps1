# ============================================
# stop-cluster.ps1 - Deletar cluster EKS
# ============================================

param(
    [string]$ClusterName = "fintech-cluster",
    [string]$Region = "us-east-1"
)

Write-Host ""
Write-Host "========================================" -ForegroundColor Red
Write-Host "  Deletando cluster EKS - Fintech" -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Red
Write-Host ""
Write-Host "Isso vai deletar o cluster e parar os custos." -ForegroundColor Yellow
$confirm = Read-Host "Confirma? (s/N)"

if ($confirm -ne "s") {
    Write-Host "Cancelado." -ForegroundColor Gray
    exit 0
}

Write-Host ""
Write-Host "Deletando cluster..." -ForegroundColor Yellow
eksctl delete cluster --name $ClusterName --region $Region

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO ao deletar cluster!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Cluster deletado! Sem mais custos." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Para recriar amanha, rode:" -ForegroundColor Cyan
Write-Host "  .\start-cluster.ps1" -ForegroundColor White
Write-Host ""
