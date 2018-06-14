import Dependencies._
import sbt.Keys._
import jp.pigumer.sbt.cloud.aws.cloudformation._
import jp.pigumer.sbt.cloud.aws.ecr.AwsecrCommands

val Region = "us-east-1"
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
    dockerBaseImage := "java:8-jdk-alpine",
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
      Alias("ecr") → CloudformationStack(
        stackName = "ecr",
        template = "ecr.yaml"
      ),
      Alias("ecscluster") → CloudformationStack(
        stackName = "ecscluster",
        template = "ecscluster.yaml"
      ),
      Alias("vpc") → CloudformationStack(
        stackName = "vpc",
        template = "vpc.yaml"
      ),
      Alias("http") → CloudformationStack(
        stackName = "http",
        template = "http.yaml",
        parameters = Map(
          "Image" → s"${(awsecrDomain in awsecr).value}/${awscfGetValue.toTask(" ECR").value}:${version.value}"
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