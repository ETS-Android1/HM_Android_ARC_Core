package org.sagebionetworks.migration

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import com.healthymedium.arc.core.SplashScreen
import com.healthymedium.arc.navigation.NavigationManager
import com.healthymedium.arc.paths.informative.ContactScreen
import com.healthymedium.arc.study.Study
import com.healthymedium.arc.utilities.PreferencesManager
import com.healthymedium.arc.utilities.ViewUtil
import kotlinx.android.synthetic.main.core_fragment_migration_splash.*
import org.sagebionetoworks.R
import org.sagebionetworks.research.sagearc.SageRestApi

class MigrationSplashScreen() : SplashScreen(), SageRestApi.MigrationCompletedListener {
    constructor(visible: Boolean) : this() {
        super.visible = visible
    }

    override fun viewLayout(): Int {
        return R.layout.core_fragment_migration_splash
    }

    override fun exit() {
        val sageResApi = Study.getRestClient() as? SageRestApi
        if (sageResApi?.userNeedsToMigrate() == true) {
            setUpdatingUi()
            sageResApi.migrateUserToSageBridge(this)
            return
        }
        super.exit()
    }

    @SuppressLint("SetTextI18n")
    fun setUpdatingUi() {
        migration_contact_us_button.visibility = View.GONE
        migration_try_again_button.visibility = View.GONE
        migration_text.visibility = View.VISIBLE
        migration_progress.visibility = View.VISIBLE
        migration_progress.max = SageRestApi.HmToSageMigration.migrationSteps
        migration_progress.progress = 0
        migration_text.text = ViewUtil.getString(R.string.updating) + " " +
                ViewUtil.getString(R.string.app_name) + "..."
        mainActivity?.window?.setBackgroundDrawable(null)
    }

    override fun progressUpdate(progress: Int) {
        migration_progress.progress = progress
    }

    override fun failure(errorString: String) {
        migration_text.text = errorString
        migration_progress.visibility = View.GONE
        showTryAgainButton()
    }

    fun showTryAgainButton() {
        migration_try_again_button.visibility = View.VISIBLE
        migration_try_again_button.setOnClickListener {
            this.exit()
            migration_try_again_button.visibility = View.GONE
        }
        migration_contact_us_button.visibility = View.VISIBLE
        migration_contact_us_button.setOnClickListener {
            NavigationManager.getInstance().open(ContactScreen())
        }
    }

    override fun success() {
        super.exit()
    }
}