terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
  }
}

provider "aws" {
  access_key = "minioaccesskey"
  secret_key = "miniosecretkey"
  region     = "us-east-1"

  endpoints {
    s3 = "http://localhost:9000"
  }

  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true
  s3_force_path_style         = true
}

resource "aws_s3_bucket" "meu_bucket" {
  bucket = "meu-bucket"
}

resource "aws_s3_bucket_acl" "meu_bucket_acl" {
  bucket = aws_s3_bucket.meu_bucket.id
  acl    = "private"
}
