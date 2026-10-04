pipeline {
agent any

stages {

    stage('Docker Build') {
        steps {
            sh '''
                docker build -t lingo-leap-be:latest .
            '''
        }
    }

    stage('Save Docker Image') {
        steps {
            sh '''
                docker save lingo-leap-be:latest \
                 -o /opt/docker/application/jenkins/lingo-leap-be.tar
            '''
    }
}

    stage('Import Image to Kubernetes') {
        steps {
            sh '''
                docker run --rm \
                    -v /run/k3s/containerd/containerd.sock:/run/k3s/containerd/containerd.sock \
                    -v /opt/docker/application:/opt/docker/application \
                    debian:13-slim \
                    bash -c "
                        apt-get update -qq &&
                        apt-get install -y -qq containerd >/dev/null &&
                        ctr --address /run/k3s/containerd/containerd.sock \
                            images import /opt/docker/application/jenkins/lingo-leap-be.tar
                    "
            '''
        }
    }

    stage('Deploy to Kubernetes') {
        steps {
            sh '''
                kubectl rollout restart deployment/lingo-leap-be \
                    -n lingo-leap

                kubectl rollout status deployment/lingo-leap-be \
                    -n lingo-leap \
                    --timeout=5m
            '''
        }
    }
}

post {
    always {
        sh '''
            echo "Removing image tar..."
            rm -f /opt/docker/application/jenkins/lingo-leap-be.tar

            echo "Cleaning Docker build cache..."
            docker builder prune -af

            echo "Removing dangling Docker images..."
            docker image prune -f

            echo "Cleanup finished."

            echo "Disk usage:"
            df -h /
        '''
    }
}
}