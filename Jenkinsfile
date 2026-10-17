pipeline {
    agent any

    stages {

        stage('Show Git Revision') {
            steps {
                sh '''
                    echo "=== Git revision ==="
                    git log -1 --oneline
                    git status --short
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build -t ghcr.io/jakub-chyla/lingo-leap-be:latest .
                '''
            }
        }

        stage('Save Docker Image') {
            steps {
                sh '''
                    docker save ghcr.io/jakub-chyla/lingo-leap-be:latest \
                        -o /opt/docker/application/jenkins/lingo-leap-be.tar
                '''
            }
        }

        stage('Import Image to Kubernetes') {
            steps {
                sh '''
                    docker run --rm \
                        -e DEBIAN_FRONTEND=noninteractive \
                        -v /run/k3s/containerd/containerd.sock:/run/k3s/containerd/containerd.sock \
                        -v /opt/docker/application:/opt/docker/application \
                        debian:13-slim \
                        bash -c "
                            apt-get update -qq &&
                            apt-get install -y -qq containerd >/dev/null &&
                            ctr --address /run/k3s/containerd/containerd.sock \
                                --namespace k8s.io \
                                images import /opt/docker/application/jenkins/lingo-leap-be.tar &&
                            ctr --address /run/k3s/containerd/containerd.sock \
                                --namespace k8s.io \
                                images list | grep 'ghcr.io/jakub-chyla/lingo-leap-be'
                        "
                '''
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    echo "=== Restarting deployment ==="
                    kubectl rollout restart deployment/lingo-leap-be -n lingo-leap

                    echo "=== Waiting for rollout ==="
                    kubectl rollout status deployment/lingo-leap-be \
                        -n lingo-leap \
                        --timeout=5m

                    echo "=== Running pods ==="
                    kubectl get pods -n lingo-leap \
                        -o custom-columns='NAME:.metadata.name,IMAGE:.spec.containers[0].image,IMAGE_ID:.status.containerStatuses[0].imageID'
                '''
            }
        }
    }

    post {
        always {
            sh '''
                echo "=== Cleanup ==="

                echo "Removing image tar..."
                rm -f /opt/docker/application/jenkins/lingo-leap-be.tar

                echo "Cleaning Docker build cache..."
                docker builder prune -af

                echo "Removing dangling Docker images..."
                docker image prune -f

                echo "=== Disk usage ==="
                df -h /

                echo "Cleanup finished."
            '''
        }
    }
}