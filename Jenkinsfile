// ==========================
// VARIABLES GLOBALES
// ==========================
def PROJECT_NAME = "ms-pedidos"
def JAVA_VERSION = "MAVEN339_JDK11_OPENJ9"
def DOCKER_IMAGE = "eminope/${PROJECT_NAME}"
def REGISTRY = "index.docker.io/v1/"
def SONAR_PROJECT_KEY = PROJECT_NAME

pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_CREDENTIALS = credentials('docker-credentials')
    }

    stages {

        // ==========================
        // CHECKOUT
        // ==========================
        stage('Checkout') {
            steps {
                checkout scm
                echo "📌 Branch actual: ${env.BRANCH_NAME}"
            }
        }

        // ==========================
        // BUILD
        // ==========================
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        // ==========================
        // TEST
        // ==========================
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // ==========================
        // SONAR (opcional pero recomendado)
        // ==========================
        stage('SonarQube') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'qa'
                    branch 'main'
                }
            }
            steps {
                withSonarQubeEnv('sonar-local') {
                    sh """
                    mvn verify sonar:sonar \
                    -Dsonar.projectKey=${SONAR_PROJECT_KEY}
                    """
                }
            }
        }

        // ==========================
        // PACKAGE
        // ==========================
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        // ==========================
        // DOCKER BUILD: sirve para crear la imagen Docker a partir del Dockerfile en el proyecto
        // Necesitamos un Dockerfile en el proyecto con la configuración para construir la imagen
        // ==========================
        stage('Docker Build') {
            steps {
                script {
                    dockerImage = docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                }
            }
        }

        // ==========================
        // DOCKER PUSH: sirve para subir la imagen Docker al registry (Docker Hub, ECR, GCR, etc.)
        // ==========================
        stage('Docker Push') {
            steps {
                script {
                    docker.withRegistry("https://${REGISTRY}", 'docker-credentials'){
                        dockerImage.push("${env.BUILD_NUMBER}")
                        dockerImage.push("latest")
                    }
                }
            }
        } 
    
    }

    // ==========================
    // POST
    // ==========================
    post {
        success {
            echo "✅ Pipeline CI exitoso"
        }
        failure {
            echo "❌ Pipeline CI falló"
        }
    }
}