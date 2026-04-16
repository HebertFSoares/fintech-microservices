# ============================================
# start-cluster.ps1 - Inicializar cluster EKS
# ============================================

param(
    [string]$ClusterName = "fintech-cluster",
    [string]$Region = "us-east-1",
    [string]$PrincipalArn = "arn:aws:iam::608420805829:user/fintech-admin"
)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Iniciando cluster EKS - Fintech" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Criar cluster
Write-Host "[1/5] Criando cluster EKS..." -ForegroundColor Yellow
eksctl create cluster `
    --name $ClusterName `
    --region $Region `
    --version 1.32 `
    --without-nodegroup

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO ao criar cluster!" -ForegroundColor Red
    exit 1
}
Write-Host "Cluster criado com sucesso!" -ForegroundColor Green

# 2. Configurar kubectl
Write-Host ""
Write-Host "[2/5] Configurando kubectl..." -ForegroundColor Yellow
aws eks update-kubeconfig --region $Region --name $ClusterName

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO ao configurar kubectl!" -ForegroundColor Red
    exit 1
}
Write-Host "kubectl configurado!" -ForegroundColor Green

# 3. Adicionar acesso IAM
Write-Host ""
Write-Host "[3/5] Configurando acesso IAM..." -ForegroundColor Yellow
aws eks create-access-entry `
    --cluster-name $ClusterName `
    --principal-arn $PrincipalArn `
    --region $Region

aws eks associate-access-policy `
    --cluster-name $ClusterName `
    --principal-arn $PrincipalArn `
    --policy-arn arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy `
    --access-scope type=cluster `
    --region $Region

Write-Host "Acesso IAM configurado!" -ForegroundColor Green

# 4. Aplicar secret
Write-Host ""
Write-Host "[4/5] Aplicando secrets..." -ForegroundColor Yellow

if (Test-Path "k8s/secret.yml") {
    kubectl apply -f k8s/secret.yml
    Write-Host "Secrets aplicados!" -ForegroundColor Green
} else {
    Write-Host "AVISO: k8s/secret.yml nao encontrado. Aplique manualmente." -ForegroundColor Yellow
}

# 5. Trigger CI/CD
Write-Host ""
Write-Host "[5/5] Disparando CI/CD via git push..." -ForegroundColor Yellow
git commit --allow-empty -m "chore: redeploy cluster"
git push origin main

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Tudo pronto! Aguarde o CI/CD (~5min)" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Acompanhe os pods com:" -ForegroundColor Cyan
Write-Host "  kubectl get pods -n fintech -w" -ForegroundColor White
Write-Host ""
