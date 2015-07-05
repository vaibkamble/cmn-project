package core.aws.task.ec2;

import com.amazonaws.services.identitymanagement.model.DeleteInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.DeleteRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.DeleteRoleRequest;
import com.amazonaws.services.identitymanagement.model.RemoveRoleFromInstanceProfileRequest;
import core.aws.client.AWS;
import core.aws.env.Context;
import core.aws.resource.ec2.InstanceProfile;
import core.aws.workflow.Action;
import core.aws.workflow.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author neo
 */
@Action("del-instance-profile")
public class DeleteInstanceProfileTask extends Task<InstanceProfile> {
    private final Logger logger = LoggerFactory.getLogger(DeleteInstanceProfileTask.class);

    public DeleteInstanceProfileTask(InstanceProfile instanceProfile) {
        super(instanceProfile);
    }

    @Override
    public void execute(Context context) throws Exception {
        String name = resource.remoteInstanceProfile.getInstanceProfileName();

        logger.info("delete instance profile and related role and policy, name={}", name);
        AWS.iam.iam.removeRoleFromInstanceProfile(new RemoveRoleFromInstanceProfileRequest()
            .withInstanceProfileName(name)
            .withRoleName(name));
        AWS.iam.iam.deleteRolePolicy(new DeleteRolePolicyRequest().withRoleName(name).withPolicyName(name));
        AWS.iam.iam.deleteRole(new DeleteRoleRequest().withRoleName(name));
        AWS.iam.iam.deleteInstanceProfile(new DeleteInstanceProfileRequest().withInstanceProfileName(name));
    }
}
