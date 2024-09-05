#!/bin/bash

set -e

ACTION_ARG=$1
ENV_ARG=$2

get_domain_name() {
	if [ ! "$ENV_ARG" ]; then
		echo "api.orasulmeu.thenoughtyfox.com"
	else
		echo "$ENV_ARG-api.orasulmeu.thenoughtyfox.com"
	fi
}

get_stack_name() {
	if [ ! "$ENV_ARG" ]; then
		echo "orasulmeu-api"
	else
		echo "orasulmeu-api-$ENV_ARG"
	fi
}

REGION=us-east-1
AWS_ACCOUNT=225530150063
ARTIFACTS_BUCKET_NAME=$(get_stack_name)-cf-artifacts
CLOUD_WATCH_LOG_GROUP_NAME=$(get_stack_name)-ecs-log-group

DOCKER_IMAGE_NAME=$(get_stack_name)
DOCKER_REPO_NAME=$(get_stack_name)-images
DOCKER_REGISTRY=$AWS_ACCOUNT.dkr.ecr.$REGION.amazonaws.com
DOCKER_IMAGE_URL=$DOCKER_REGISTRY/$DOCKER_REPO_NAME
DOCKER_IMAGE_LATEST=$DOCKER_IMAGE_URL:latest

ENV_FILE_NAME=.env
ENV_S3_BUCKET_KEY=.env
ENV_S3_BUCKET_NAME=$(get_stack_name)-env
ENV_S3_ARN=arn:aws:s3:::$ENV_S3_BUCKET_NAME/$ENV_S3_BUCKET_KEY

HOSTED_ZONE_ID=Z032982169MDEWMQDI8M
CONTAINER_PORT=8999
VPC_ID=vpc-080fd79f9118ef947
SUBNET_ID_1=subnet-0b6f27c038e002e55
SUBNET_ID_2=subnet-042c0f70538a16ff9

print_green() {
	echo -e "\033[1;32m$1\033[0m"
}

print_red() {
	echo -e "\033[1;31m$1\033[0m"
}

check_docker() {
	if ! docker info >/dev/null 2>&1; then
		print_red "Docker does not seem to be running"
		exit
	fi
}

check_aws_cli() {
	if ! command -v aws &> /dev/null
	then
		print_red "AWS CLI not installed"
		exit
	fi
}

check_npm() {
	if ! command -v npm &> /dev/null
	then
		print_red "NPM not installed"
		exit
	fi
}

get_current_account_id() {
	local ACCOUNT_ID=$(aws sts get-caller-identity | python ./aws/get_account_id.py)
	echo $ACCOUNT_ID
}

get_cluster_name() {
	local NAME=$(
		aws cloudformation describe-stacks \
			--region $REGION \
			--stack-name $(get_stack_name) \
			--query 'Stacks[0].Outputs[?OutputKey==`ClusterName`].OutputValue' \
			--output text
	)
	echo $NAME
}

get_service_name() {
	local NAME=$(
		aws cloudformation describe-stacks \
			--region $REGION \
			--stack-name $(get_stack_name) \
			--query 'Stacks[0].Outputs[?OutputKey==`ServiceName`].OutputValue' \
			--output text
	)
	echo $NAME
}

should_create_cloudwatch_logs_group() {
	local QUERY='logGroups[?logGroupName==`'$CLOUD_WATCH_LOG_GROUP_NAME'`].arn'
	local EXISTING_GROUP_ARN=$(aws logs describe-log-groups \
		--region $REGION \
		--log-group-name-prefix $CLOUD_WATCH_LOG_GROUP_NAME \
		--query $QUERY \
		--no-paginate \
		--output text
	)
	[ -z $EXISTING_GROUP_ARN ] && echo "true" || echo "false"
}

check_aws_credentials() {
	if [ "$(get_current_account_id)" != "$AWS_ACCOUNT" ]; then
		print_red "Wrong account, please rerun using 'thenoughtyfox' AWS account credentials"
		exit
	fi
}

make_env_bucket() {
	aws s3 mb s3://$ENV_S3_BUCKET_NAME --region $REGION
	aws s3api put-bucket-versioning \
		--bucket $ENV_S3_BUCKET_NAME \
		--versioning-configuration Status=Enabled \
		> /dev/null
}

authenticate_docker() {
	print_green "Authenticating docker..."
	aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $DOCKER_REGISTRY
}

build_docker_image() {
	print_green "Building docker image..."
	ssh-add # this assumes that the GitHub private key is the default one (id_rsa)
	DOCKER_BUILDKIT=1 docker build --platform linux/amd64 --no-cache --ssh default --tag $DOCKER_IMAGE_NAME .
}

tag_docker_image() {
	print_green "Tagging docker image..."
	IMAGE_ID=$(docker images $DOCKER_IMAGE_NAME --format='{{.ID}}' | head -1)
	docker tag $IMAGE_ID $DOCKER_IMAGE_LATEST
}

create_ecr_repository() {
	print_green "Creating ECR repository if needed..."
	aws ecr describe-repositories \
		--region $REGION \
		--repository-names $DOCKER_REPO_NAME \
		> /dev/null || \
	aws ecr create-repository \
		--region $REGION \
		--repository-name $DOCKER_REPO_NAME \
		--encryption-configuration encryptionType=KMS \
		> /dev/null
	aws ecr put-lifecycle-policy \
		--region $REGION \
		--repository-name $DOCKER_REPO_NAME \
		--lifecycle-policy-text "file://$PWD/aws/ecr_lifecycle_policy.json" \
		> /dev/null
}

push_docker_image() {
	print_green "Pushing docker image to repository..."
	docker push $DOCKER_IMAGE_LATEST
}

upload_env_file() {
	print_green "Uploading ${ENV_FILE_NAME} file to S3..."
	aws s3 cp $ENV_FILE_NAME s3://$ENV_S3_BUCKET_NAME/$ENV_S3_BUCKET_KEY
}

make_buckets() {
	aws s3 mb s3://$ARTIFACTS_BUCKET_NAME --region $REGION
	aws s3api put-bucket-versioning \
		--bucket $ARTIFACTS_BUCKET_NAME \
		--versioning-configuration Status=Enabled \
		> /dev/null
}

package() {
	print_green "Packing stack..."
	aws cloudformation package \
		--region $REGION \
		--template-file cloudformation.yaml \
		--s3-bucket $ARTIFACTS_BUCKET_NAME \
		--output-template-file packaged.template
}

deploy() {
	print_green "Deploying stack..."
	aws cloudformation deploy \
		--region $REGION \
		--stack-name $(get_stack_name) \
		--template-file packaged.template \
		--capabilities CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
		--parameter-overrides \
		DomainName=$(get_domain_name) \
		HostedZoneID=$HOSTED_ZONE_ID \
		TaskImage=$DOCKER_IMAGE_LATEST \
		EnvFileARN=$ENV_S3_ARN \
		ContainerPort=$CONTAINER_PORT \
		VpcId=$VPC_ID \
		SubnetId1=$SUBNET_ID_1 \
		SubnetId2=$SUBNET_ID_2 \
		CreateLogsGroup=$(should_create_cloudwatch_logs_group) \
		LogsGroupName=$CLOUD_WATCH_LOG_GROUP_NAME
}

update() {
	print_green "Creating change set..."
	CHANGE_SET_ID=$(aws cloudformation create-change-set \
		--region $REGION \
		--stack-name $(get_stack_name) \
		--change-set-name latest-$(date +%s) \
		--change-set-type UPDATE \
		--template-body "file://$PWD/packaged.template" \
		--capabilities CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
		--parameters \
		ParameterKey=DomainName,ParameterValue=$(get_domain_name) \
		ParameterKey=HostedZoneID,ParameterValue=$HOSTED_ZONE_ID \
		ParameterKey=TaskImage,ParameterValue=$DOCKER_IMAGE_LATEST \
		ParameterKey=EnvFileARN,ParameterValue=$ENV_S3_ARN \
		ParameterKey=ContainerPort,ParameterValue=$CONTAINER_PORT \
		ParameterKey=VpcId,ParameterValue=$VPC_ID \
		ParameterKey=SubnetId1,ParameterValue=$SUBNET_ID_1 \
		ParameterKey=SubnetId2,ParameterValue=$SUBNET_ID_2 \
		ParameterKey=CreateLogsGroup,ParameterValue=$(should_create_cloudwatch_logs_group) \
		ParameterKey=LogsGroupName,ParameterValue=$CLOUD_WATCH_LOG_GROUP_NAME \
		| python ./aws/get_change_set_id.py
	)

	execute_change_set $CHANGE_SET_ID
}

force_new_deployment() {
	print_green "Forcing new deployment..."
	aws ecs update-service \
		--region $REGION \
		--cluster $(get_cluster_name) \
		--service $(get_service_name) \
		--force-new-deployment \
		> /dev/null
}

execute_change_set() {
	print_green "Waiting for change set to finish creating..."
	aws cloudformation wait change-set-create-complete \
		--region $REGION \
		--stack-name $(get_stack_name) \
		--change-set-name $1

	print_green "Executing change set..."
	aws cloudformation execute-change-set \
		--region $REGION \
		--stack-name $(get_stack_name) \
		--change-set-name $1 \
		> /dev/null

	print_green "Waiting for stack to finish updating..."
	aws cloudformation wait stack-update-complete \
		--region $REGION \
		--stack-name $(get_stack_name)
}

build_and_upload_docker_image() {
	check_docker
	authenticate_docker
	build_docker_image
	tag_docker_image
	create_ecr_repository
	push_docker_image
	make_env_bucket
	upload_env_file
}

create_stack() {
	build_and_upload_docker_image
	make_buckets
	package
	deploy
}

update_stack() {
	make_buckets
	package
	update
}

deploy_new_image() {
	build_and_upload_docker_image
	force_new_deployment
}

delete_stack() {
	print_green "Deleting stack..."
	aws cloudformation delete-stack \
		--region $REGION \
		--stack-name $(get_stack_name) \
		> /dev/null

	print_green "Waiting for stack to finish deleting..."
	aws cloudformation wait stack-delete-complete \
		--region $REGION \
		--stack-name $(get_stack_name)
}

check_aws_cli
check_aws_credentials
check_npm

case "$ACTION_ARG" in
  "create" ) create_stack ;;
  "update" ) update_stack ;;
  "deploy" ) deploy_new_image ;;
  "delete" ) delete_stack ;;
  * ) print_red "Please specify action parameter: ./aws-deploy create | update | deploy | delete" ;;
esac