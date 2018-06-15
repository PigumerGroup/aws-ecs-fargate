AWS Fargate
===========

# Create network, repository and cluster

```
$ BUCKET_NAME=<YOUR BUCKET NAME> sbt
sbt> awscfCreateBucket create-bucket-stackname
sbt> awscfUploadTemplates
sbt> awscfCreateStack vpc
sbt> awscfCreateStack alb 
sbt> awscfCreateStack ecr
sbt> awscfCreateStack ecscluster
```

# Build and push

```
sbt> docker:stage
sbt> docker:publishLocal
sbt> awsecr::awsecrLogin
sbt> awsecrPush
```

# Run

```
sbt> awscfCreateStack http
```