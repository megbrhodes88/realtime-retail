terraform {
    required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
    local = {
        source = "hashicorp/local"
        version = "2.2.3"
    }
    template = {
        source = "hashicorp/template"
        version = "2.2.0"
    }
  }
}
provider "aws" {
    region = "us-east-2"
}
resource "aws_vpc" "vpc_main" {
    cidr_block = "10.0.0.0/16"
    tags = {
      "Name" = "eks-comm-se-vpc"
    }
}
resource "aws_subnet" "public_subnet_1" {
    vpc_id = aws_vpc.vpc_main.id
    cidr_block = "10.0.1.0/24"
    map_public_ip_on_launch = true
    tags = {
      "Name" = "eks-comm-se-vpc-pub-sub-1"
    }
}
resource "aws_subnet" "public_subnet_2" {
    vpc_id = aws_vpc.vpc_main.id
    cidr_block = "10.0.2.0/24"
    map_public_ip_on_launch = true
    tags = {
      "Name" = "eks-comm-se-vpc-pub-sub-2"
    }
}
resource "aws_subnet" "public_subnet_3" {
    vpc_id = aws_vpc.vpc_main.id
    cidr_block = "10.0.3.0/24"
    map_public_ip_on_launch = true
    tags = {
      "Name" = "eks-comm-se-vpc-pub-sub-3"
    }
}
data "template_file" "eks_cluster_config_template" {
    template = "${file("../k8s/eks/cluster.tmpl")}"
    vars = {
        vpc_id = aws_vpc.vpc_main.id
        public_subnet_1_id = aws_subnet.public_subnet_1.id
        public_subnet_2_id = aws_subnet.public_subnet_2.id
        public_subnet_3_id = aws_subnet.public_subnet_3.id
    }
}

resource "local_file" "eks_cluster_config_yaml" {
    filename = "../k8s/eks/cluster.yaml"
    content = data.template_file.eks_cluster_config_template.rendered
}


