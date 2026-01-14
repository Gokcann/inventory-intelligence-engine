#!/bin/bash
set -e

echo "🚀 IIE Kubernetes Deployment"
echo "============================"

# Build Docker image
echo "📦 Building Docker image..."
docker build -t iie:latest .

# Apply Kubernetes manifests
echo "☸️  Applying Kubernetes manifests..."
kubectl apply -k k8s/

# Wait for pods
echo "⏳ Waiting for pods to be ready..."
kubectl wait --for=condition=ready pod -l app=iie -n iie --timeout=120s

# Show status
echo "✅ Deployment complete!"
kubectl get pods -n iie
echo ""
kubectl get svc -n iie

echo ""
echo "📌 Access the application:"
echo "   kubectl port-forward svc/iie-service 8080:80 -n iie"
