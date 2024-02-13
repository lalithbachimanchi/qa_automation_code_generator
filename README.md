

# Generate API Automation Code for QA

## Purpose/Objective 

This tool is aimed to generate REST API automation test code from Swagger documentation.
There are two jobs configured on Jenkins: Code Generator and Code Runner
With Code Generator, we can generate automation code and file will be availble as build artifact to be downloaded
With Code Runner, we can run the generated automation code and see the junit test reports right away


## GPT credentials integration 

On .env file update following environment variables
```
OPENAI_API_KEY
OPENAI_ORG_ID
```

## TODO
Support Multiple Languages and Frameworks

### References: 

1. Jenkins Pipeline Build step : https://www.jenkins.io/doc/pipeline/steps/pipeline-build-step/ 


### Software Required to run Jenkins: 

1. Docker
2. Docker-compose
