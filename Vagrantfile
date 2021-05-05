
# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.

Vagrant.configure("2") do |config|

  # Every Vagrant development environment requires a box.
  config.vm.box = "ubuntu/xenial64"
  # This is the name vagrant outputs on the console and logs.
  config.vm.define "vm_docker_flink_sql"

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine.
  # Map port 9092 of the VM to port 9092 of the host (this is the port used by Kafka).
  config.vm.network "forwarded_port", guest: 9092, host: 9092

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  config.vm.provider "virtualbox" do |vb|
    # This is the name VirtualBox uses for the VM.
    vb.name = "vm_docker_flink_sql"
    # Set 2GB of RAM for the VM.
    vb.memory = "2048"
  end

  # Enable provisioning with Docker and Docker Compose.
  config.vm.provision :docker
  config.vm.provision :docker_compose, yml: "/vagrant/docker-compose.yml", run: "always"

end
