package com.nhq.authenticator.data.contract

object SitesAvailable {
    @JvmStatic
    val sites = mutableListOf<String>()

    init {
        sites.add("Google")
        sites.add("Microsoft")
        sites.add("Facebook")
        sites.add("Twitter")
    }
}