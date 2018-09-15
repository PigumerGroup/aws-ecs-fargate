import Dependencies._
import sbt.Keys._
import jp.pigumer.sbt.cloud.aws.cloudformation._
import jp.pigumer.sbt.cloud.aws.ecr.AwsecrCommands

val Region = "ap-northeast-1"
val BucketName = sys.env.get("BUCKET_NAME") // YOUR BUCKET NAME

val awsecrPush = taskKey[Unit]("Push")

lazy val root = (project in file("."))
  .enablePlugins(CloudformationPlugin, JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(
    organization := "com.pigumer",
    name := "http",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaStream
    )
  ).settings(
    dockerBaseImage := "openjdk:8-jre-alpine",
    dockerExposedPorts := Seq(8080, 9010),
    mainClass in assembly := Some("com.pigumer.http.HelloWorld")
  ).settings(
    awscfSettings := AwscfSettings(
      region = Region,
      bucketName = BucketName,
      templates = Some(file("cloudformation"))
    )
  ).settings(
    awscfStacks := Stacks(
      Alias("iam") → CloudformationStack(
        stackName = "aws-ecs-fargate-iam",
        template = "iam.yaml",
        capabilities = Seq("CAPABILITY_NAMED_IAM")
      ),
      Alias("codebuild") → CloudformationStack(
        stackName = "aws-ecs-fargate-codebuild",
        template = "codebuild.yaml",
        parameters = Map(
          "Location" → BucketName.get
        )
      ),
      Alias("vpc") → CloudformationStack(
        stackName = "aws-ecs-fargate-vpc",
        template = "vpc.yaml"
      ),
      Alias("alb") → CloudformationStack(
        stackName = "aws-ecs-fargate-alb",
        template = "alb.yaml"
      ),
      Alias("ecr") → CloudformationStack(
        stackName = "aws-ecs-fargate-ecr",
        template = "ecr.yaml"
      ),
      Alias("ecscluster") → CloudformationStack(
        stackName = "aws-ecs-fargate-ecscluster",
        template = "ecscluster.yaml"
      ),
      Alias("http") → CloudformationStack(
        stackName = "aws-ecs-fargate-http",
        template = "http.yaml",
        parameters = Map(
          "Image" → s"${(awsecrDomain in awsecr).value}/http:${version.value}"
        ),
        capabilities = Seq("CAPABILITY_IAM")
      )
    )
  ).settings(
    awsecrPush := {
      val docker = (awsecrDockerPath in awsecr).value
      val source = s"${packageName.value}:${version.value}"
      val target = s"${(awsecrDomain in awsecr).value}/${awscfGetValue.toTask(" ECR").value}:${version.value}"
      AwsecrCommands.tag(docker, source, target)
      AwsecrCommands.push(docker, target)
    }
  )