package com.containersolutions.mesos;

import com.containersol.minimesos.cluster.MesosCluster;
import com.containersol.minimesos.marathon.Marathon;
import com.containersol.minimesos.mesos.ClusterArchitecture;
import com.containersol.minimesos.mesos.DockerClientFactory;
import com.github.dockerjava.api.DockerClient;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Runs the framework via minimesos.
 */
public class MinimesosRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        DockerClient dockerClient = DockerClientFactory.build();

        MesosCluster cluster = new MesosCluster(
                new ClusterArchitecture.Builder()
                        .withZooKeeper()
                        .withMaster()
                        .withMarathon(zooKeeper -> new Marathon(dockerClient, zooKeeper, false))
                        .withSlave("ports(*):[8080-8080]").build()
        );
        cluster.start();

        Thread.sleep(5_000);

        cluster.deployMarathonApp(IOUtils.toString(new ClassPathResource("sample.json").getInputStream()));

        System.out.println("The framework is running on the Mesos Master at " + cluster.getMasterContainer().getIpAddress());
    }
}
