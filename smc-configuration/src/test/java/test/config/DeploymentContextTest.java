package test.config;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DeploymentContext;
import com.netflix.config.source.ZooKeeperConfigurationSource;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/11/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeploymentContextTest {

    public static void main(String[] args){
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        System.out.println(ConfigurationManager.getDeploymentContext().getDeploymentEnvironment());
        System.out.println(ConfigurationManager.getDeploymentContext().getDeploymentEnvironment());


    }
}
