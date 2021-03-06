package core.aws.remote.as;

import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.ScalingPolicy;
import core.aws.client.AWS;
import core.aws.env.Environment;
import core.aws.resource.Resources;
import core.aws.resource.as.ASGroup;
import core.aws.resource.as.AutoScalingPolicy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author neo
 */
public class ASGroupLoader {
    private final Resources resources;
    private final Environment env;

    public ASGroupLoader(Resources resources, Environment env) {
        this.resources = resources;
        this.env = env;
    }

    public void load() {
        String prefix = env.name + "-";

        // find all AS group with prefix
        List<AutoScalingGroup> asGroups = AWS.as.listASGroups().stream()
            .filter(group -> group.getAutoScalingGroupName().startsWith(prefix))
            .collect(Collectors.toList());

        if (asGroups.isEmpty()) return;

        // load remote launch config in one request to maximize the speed
        List<String> launchConfigNames = asGroups.stream().map(AutoScalingGroup::getLaunchConfigurationName).collect(Collectors.toList());
        Map<String, LaunchConfiguration> configs = AWS.as.describeLaunchConfigs(launchConfigNames);

        for (AutoScalingGroup remoteASGroup : asGroups) {
            String asGroupName = remoteASGroup.getAutoScalingGroupName();
            String asGroupId = asGroupName.substring(prefix.length());
            Optional<ASGroup> result = resources.find(ASGroup.class, asGroupId);
            ASGroup asGroup = result.isPresent() ? result.get() : resources.add(new ASGroup(asGroupId));
            asGroup.remoteASGroup = remoteASGroup;
            asGroup.launchConfig.remoteLaunchConfig = configs.get(remoteASGroup.getLaunchConfigurationName());
            asGroup.foundInRemote();

            List<ScalingPolicy> remotePolicies = AWS.as.describeScalingPolicies(asGroupName);
            for (ScalingPolicy remotePolicy : remotePolicies) {
                String policyId = remotePolicy.getPolicyName();
                Optional<AutoScalingPolicy> policyResult = resources.find(AutoScalingPolicy.class, policyId);
                AutoScalingPolicy policy = policyResult.isPresent() ? policyResult.get() : resources.add(new AutoScalingPolicy(policyId));
                policy.remotePolicy = remotePolicy;
                policy.foundInRemote();
            }
        }
    }
}