AWS Fargate
===========

```
$ BUCKET_NAME=<YOUR BUCKET NAME> sbt
sbt> awscfCreateBucket create-bucket-stackname
sbt> awscfUploadTemplates
sbt> awscfCreateStack vpc
sbt> awscfCreateStack ecr
sbt> awscfCreateStack ecscluster
sbt> docker:stage
sbt> docker:publishLocal
sbt> awsecr::awsecrLogin
sbt> awsecrPush
sbt> awscfCreateStack http
```