package com.mwagent.feature.onboarding.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mwagent.R
import com.mwagent.feature.onboarding.PageScaffold
import com.mwagent.feature.onboarding.reveal
import com.mwagent.feature.onboarding.sims.AskScene

@Composable
fun AboutPage() {
    PageScaffold(
        title = stringResource(R.string.onboarding_about_title),
        body = stringResource(R.string.onboarding_about_body),
    ) {
        AskScene(Modifier.reveal(2))
    }
}
