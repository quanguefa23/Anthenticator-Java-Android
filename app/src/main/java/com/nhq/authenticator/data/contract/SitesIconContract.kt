package com.nhq.authenticator.data.contract

import com.nhq.authenticator.R
import java.util.*

object SitesIconContract {
    private var mapSiteNameWithIcon = mutableMapOf<String, Int?>()

    init {
        mapSiteNameWithIcon["google"] = R.drawable.ic_google
        mapSiteNameWithIcon["microsoft"] = R.drawable.ic_microsoft
        mapSiteNameWithIcon["facebook"] = R.drawable.ic_facebook
        mapSiteNameWithIcon["twitter"] = R.drawable.ic_twitter
    }

    @JvmStatic
    fun getIconId(siteName: String): Int? {
        var siteName = siteName
        siteName = siteName.toLowerCase(Locale.ROOT)
        return if (mapSiteNameWithIcon.containsKey(siteName)) mapSiteNameWithIcon[siteName]
            else R.drawable.ic_web
    }
}