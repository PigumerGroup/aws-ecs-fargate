![Build Status](https://codebuild.ap-northeast-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiT3VMZHM5VXg2YmtFYmdHb1krQm5WRnBzdGxVek1XSld2UUhEbTZlbjBHOVdmeVA1aDhpVlM5MGRZMzZLd2FNTW5ZdytuUHlOZHdWWFhMR3NNV2hsTzBBPSIsIml2UGFyYW1ldGVyU3BlYyI6Ikx5eHY0bE9iWHZwd2lxZEQiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)

AWS Fargate
===========

# Create network, repository and cluster

```
$ BUCKET_NAME=<YOUR BUCKET NAME> sbt
sbt> awscfCreateBucket create-bucket-stackname
sbt> awscfUploadTemplates
sbt> awscfCreateStack iam
sbt> awscfCreateStack codebuild
sbt> awscfCreateStack vpc
sbt> awscfCreateStack alb 
sbt> awscfCreateStack ecr
sbt> awscfCreateStack ecscluster
```

# Build

```
sbt> docker:stage
sbt> docker:publishLocal
```

# Push to repository

```
sbt> awsecr::awsecrLogin
sbt> awsecrPush
```

# Run

```
sbt> awscfCreateStack http
```