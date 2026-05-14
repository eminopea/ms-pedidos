pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        BRANCH_NAME = "${env.BRANCH_NAME}"
    }

    stages {

        stage('Info') {
            steps {
                echo "Branch actual: ${BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube') {
            when {
                branch 'develop'
            }
            steps {
                sh '''
                mvn sonar:sonar \
                -Dsonar.projectKey=${BRANCH_NAME} \
                -Dsonar.host.url=http://sonarqube:9000 \
                -Dsonar.login=$SONAR_TOKEN
                '''
            }
        }

        stage('Deploy DEV') {
            when {
                branch 'develop'
            }
            steps {
                sh 'echo "Deploy en entorno DEV"'
            }
        }

        stage('Deploy QA') {
            when {
                branch 'qa'
            }
            steps {
                sh 'echo "Deploy en entorno QA"'
            }
        }

        stage('Deploy PROD') {
            when {
                branch 'main'
            }
            steps {
                sh 'echo "Deploy en PRODUCCIÓN 🚨"'
            }
        }
    }
}