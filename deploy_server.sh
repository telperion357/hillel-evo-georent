#!/usr/bin/env bash

target=georent-back.jar
jar_path=build/libs/${target}
bucket=s3://geo-rent-bucket/server-jars

./gradlew clean build

export AWS_ACCESS_KEY_ID=AKIA4BDR5OXQBHACLXU7
export AWS_SECRET_ACCESS_KEY=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

echo 'uploading artifact to S3'
aws s3 cp ${jar_path} ${bucket}/${target}
aws s3 cp src/main/resources/application.yml ${bucket}/application.yml

#echo 'rebooting server'
#aws ec2 reboot-instances --instance-ids i-00282fece29e2f707