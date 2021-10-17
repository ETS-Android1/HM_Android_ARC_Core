package org.sagebionetworks.researchstack;

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.sagebionetworks.researchstack.framework.SageTaskFactory;
import org.sagebionetworks.researchstack.backbone.factory.IntentFactory;
import org.sagebionetworks.researchstack.backbone.task.Task;

import java.util.UUID;

import javax.inject.Inject;

public class SageResearchStackTaskLauncher {
    private final SageTaskFactory mpTaskFactory;

    @Inject
    public SageResearchStackTaskLauncher(@NonNull SageTaskFactory mpTaskFactory) {
        this.mpTaskFactory = checkNotNull(mpTaskFactory);
    }

    /**
     * @param activity
     * @param taskIdentifier
     * @param taskRunUUID
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public void launchTask(@NonNull Activity activity, @NonNull String taskIdentifier,
            @Nullable UUID taskRunUUID, int requestCode) {
        // no-op
    }
}
