cmn is a AWS resource manager.

# TODO:
* rabbitmq conf is tied to ip, after bake, all conf is gone, if specify node name via https://www.rabbitmq.com/man/rabbitmq-env.conf.5.man.html
  the name will be conflict in cluster, think about how to manage rabbitmq cluster later
* general linux tuning, like ulimit to sys 

# Known issues
none

# Change log
### 6/26/2015 - 7/2/2015 (1.5.0)
* updated AWS sdk version 
* update nginx role to support custom conf
* SQS supports China region
* update EC2.availabilityZones() to return available ones

### 6/25/2015 (1.4.9)
* provision will use ami() package-dir/playbook by default

### 6/22/2015 - 6/24/2015 (1.4.8)
* support cn-north-1 region, which doesn't have M4/C4 instances, bake AMI will use M3.Large
* fixed unnecessary space in local cert will cause re-update cert.  

### 6/17/2015 (1.4.7)
* update rabbitmq/elasticsearch roles, to add log rotate support and other config
* recreate instance if new instance profile added or deleted
* update elasticsearch to 1.6.0
* update default nginx proxy setting to forward http_port, to make core-ng can construct requestURL

### 6/12/2015 - 6/16/2015 (1.4.6)
* support new M4 instances
The trends of AWS is to use VPC/HVM instance, and no more ephemeral volumes, and enable EBS-optimized by default
in future we should only use t2/m4/c4 instances, and use HVM ubuntu linux, this will simplify cmn
* remove EBS-optimized configuration
* remove mount ephemeral disk as SWAP ec2 scripts,
* refactory log folder and logrotate script to make sure shutdown hook gz log properly
* instance deploy will wait until ELB attach done

### 6/9/2015-6/10/2015 (1.4.5)
* make VPC required
* env config added, "bake-subnet-id:" for account doesn't have default VPC
* updated tomcat/supervisor/nginx role, not start on bake

### 6/6/2015 (1.4.4)
* update rabbitmq role

### 5/21/2015 (start 1.4.4)
* add supervisor support for core-ng application

### 5/21/2015 (1.4.3)
* fixed ASG deployment issue, with manually updated ASG size, the deployment may not use right maxSize

### 5/16/2015 (start 1.4.3)
* updated mongodb role to latest 3.0 and config

### 5/13/2015 (1.4.2)
* add port range for SG, for passive FTP
```
#!yml
- security-group[dev]:
    ingress:
      - {cidr: 0.0.0.0/0, protocol: [ssh, http, 30000-40000]}
```

* support subscribe queue to topic
```
#!yml
- sqs[queue-1]:

- sqs[queue-2]:

- sns[topic]:
    sqs-subscription: [queue-1, queue-2]
```

### 5/7/2015 (start 1.4.2)
* explicitly shutdown old instance at end of ASG deploy, as ASG has issue may not choose oldest instance sometimes (probably because of multi-az) 
* bake AMI can return "failed" state, break if it's failed.
* updated elasticsearch role to allow groovy script

### 5/6/2015 (1.4.1)
* added build and jenkins role to provide simple support for build server

### 4/30/2015 (continue 1.4.1)
* simplified attach to elb task, now "deploy instance" will attach to ELB in time.

### 4/29/2015 (start 1.4.1)
* structure refactory, simplify task design, prepare for further refactory

### 4/28/2015 (1.4.0)
* add java monitor to collect heap usage and thread count

### 4/28/2015 (1.3.9)
* !!! reorganized ec2/logrotate ansible roles, it's better to rebake baseAMI
* validate allowed/required param from command line input

### 4/27/2015 (continue 1.3.9)
* update SSH runner to keep alive with SSH session
* add memUsage cloudwatch metrics in EC2

### 4/24/2015 (start 1.3.9, wait for testing later)
* deploy ASG, check ELB state of new instances if applicable.

### 4/23/2015
* removed memcached and activemq roles, don't use them anymore, and you can put into your env/ansible folder for custom roles.

### 4/22/2015 (1.3.8)
* update tomcat default roles to support use "cmn provision" to release for non-ASG instances
* update ASG always use OldestInstance as termination policy, due to OldestLaunchConfig may not work properly when config was deleted during deployment

### 4/21/2015 (1.3.7)
* enable ELB multi-zone load balancing for multi-az

### 4/20/2015 (1.3.6)
* refactored AWS client to use plain design for simplicity and flexibility
* removed disabled resource support, always check all since most of them will be used
* support updating iam profile policy

### 4/18/2015
* update health check settings for high load scenario

### 4/17/2015 (1.3.5)
* only use HTTP connector of tomcat (according to perf test on AWS)
* nginx set x-forwarded-proto smartly, enable keep alive between nginx and tomcat
* ELB always forward to http port

### 4/15/2015 (1.3.3/1.3.4)
* bake: use --resume-bake=true to auto pick previous instance,
* bake: bake will clean up previous failed instance/sg/key
* deployment: make ASG deployment more robust

### 4/13/2015 (1.3.2)
* support "scheme: internal" for ELB in public subnet used for internal
* fix delete VPC should depends on delete SG

### 3/23/2015 (1.3.0)
* support deploy goal
* removed instance bootstrap script support, (not needed anymore)

### 3/16/2015
* make jdk 8 as default
* support mac os
* fix: auto assign public ip to AS group in public subnet