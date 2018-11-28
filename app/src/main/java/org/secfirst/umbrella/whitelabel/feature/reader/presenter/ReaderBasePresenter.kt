package org.secfirst.umbrella.whitelabel.feature.reader.presenter

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView

interface ReaderBasePresenter<V : ReaderView, I : ReaderBaseInteractor> : BasePresenter<V, I> {

    fun submitFetchRss()

    fun submitInsertRss(rss: RSS)

    fun submitDeleteRss(rss: RSS)

    fun submitDeleteFeedLocation()

    fun submitInsertFeedSource(feedSources: List<FeedSource>)

    fun submitFeedRequest(feedLocation: FeedLocation,
                          feedSources: List<FeedSource>,
                          isFirstRequest: Boolean = false)

    fun submitFeedLocation(feedLocation: FeedLocation)

    fun submitInsertFeedLocation(feedLocation: FeedLocation)

    fun submitPutRefreshInterval(position: Int)

    fun prepareView()

}