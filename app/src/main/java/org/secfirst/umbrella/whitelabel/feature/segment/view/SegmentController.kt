package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.view.*
import com.bluelinelabs.conductor.RouterTransaction
import com.commonsware.cwac.anddown.AndDown
import encodeToBase64
import info.guardianproject.iocipher.VirtualFileSystem
import kotlinx.android.synthetic.main.segment_view.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.covertToHTML
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.SegmentAdapter
import org.secfirst.umbrella.whitelabel.misc.IOCipherContentProvider.Companion.SHARE_FILE
import org.secfirst.umbrella.whitelabel.misc.initGridView
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject


class SegmentController(bundle: Bundle) : BaseController(bundle), SegmentView {


    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val segmentClick: (Int) -> Unit = this::onSegmentClicked
    private val checklistFavoriteClick: (Boolean) -> Unit = this::onChecklistFavoriteClick
    private val segmentFavoriteClick: (Markdown) -> Unit = this::onSegmentFavoriteClick
    private val checklistShareClick: () -> Unit = this::onChecklistShareClick
    private val segmentShareClick: (Markdown) -> Unit = this::onSegmentShareClick
    private val footClick: (Int) -> Unit = this::onFootClicked
    private val markdowns by lazy { args.getParcelableArray(EXTRA_SEGMENT) as Array<Markdown> }
    private val checklist by lazy { args.getParcelable(EXTRA_CHECKLIST) as Checklist? }
    private val titleTab by lazy { args.getString(EXTRA_SEGMENT_TAB_TITLE) }
    private var indexTab = 0
    lateinit var hostSegmentTabControl: HostSegmentTabControl

    constructor(markdowns: List<Markdown>, titleTab: String, checklist: Checklist?) : this(Bundle().apply {
        putParcelableArray(EXTRA_SEGMENT, markdowns.toTypedArray())
        putParcelable(EXTRA_CHECKLIST, checklist)
        putString(EXTRA_SEGMENT_TAB_TITLE, titleTab)
    })

    override fun onInject() {
        DaggerSegmentComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onAttach(this)
        showSegmentView(markdowns.toList())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.segment_view, container, false)
    }

    private fun showSegmentView(markdowns: List<Markdown>) {
        initSegmentView(markdowns)
    }

    private fun initSegmentView(markdowns: List<Markdown>) {
        val sortedMarkdowns = markdowns.sortedWith(compareBy { it.index })
        val segmentAdapter = SegmentAdapter(segmentClick, footClick,
                checklistShareClick, segmentShareClick,
                checklistFavoriteClick, segmentFavoriteClick, checklist, sortedMarkdowns.toMutableList())

        segmentRecyclerView?.initGridView(segmentAdapter)
        setFooterList(segmentAdapter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        }
        return true
    }

    private fun setFooterList(segmentAdapter: SegmentAdapter) {
        val manager = segmentRecyclerView?.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (segmentAdapter.isChecklistFoot(position)) manager.spanCount else 1
            }
        }
    }

    override fun showSegmentDetail(markdown: Markdown) {
        parentController?.router?.pushController(RouterTransaction.with(SegmentDetailController(markdown)))
    }

    private fun onChecklistFavoriteClick(isFavorite: Boolean) {
        checklist?.favorite = isFavorite
        if (checklist != null)
            presenter.submitChecklistFavorite(checklist!!)
    }


    private fun onSegmentFavoriteClick(markdown: Markdown) {
        presenter.submitMarkdownFavorite(markdown)
    }

    private fun onChecklistShareClick() {
        val checklistHtml = checklist?.covertToHTML()
        val intent = Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:?subject=Checklist&body=${Uri.encode(checklistHtml)}"))
        startActivity(intent)

    }

    private fun onSegmentShareClick(markdown: Markdown) {
        val andDown = AndDown()
        val result = andDown.markdownToHtml(markdown.text, AndDown.HOEDOWN_EXT_QUOTE, 0)
        val doc = Jsoup.parse(result)

        val img: Elements = doc.getElementsByTag("img")
        lateinit var src: String
        lateinit var base64img: String



        for (el in img) {
            src = el.absUrl("src").replace("file://", "")
            base64img = encodeToBase64(File(src))
            src = "data:image/png;base64," + base64img
            el.attr("src", src)

        }

        val filename = markdown.title
        val f = java.io.File(UmbrellaApplication.instance.cacheDir, SHARE_FILE)
        if (!f.exists()) {
            try {
                f.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        val path = f.path

        val vfs = VirtualFileSystem.get()
        vfs.containerPath = path
        try {
            if (!vfs.isMounted)
                vfs.mount("Umbrella")
            if (!vfs.isMounted)
                vfs.createNewContainer(path)

            val file = File(UmbrellaApplication.instance.cacheDir, filename + ".html")
            try {
                val writer = BufferedWriter(FileWriter(file))
                writer.write(doc.toString())
                writer.flush()
                writer.close()
                val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file)
                val shareIntent = ShareCompat.IntentBuilder.from(activity)
                        .setType(activity!!.contentResolver.getType(uri))
                        .setStream(uri)
                        .intent

                //Provide read access
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, markdown.title)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val pm = activity!!.packageManager

                if (shareIntent.resolveActivity(pm) != null) {
                    startActivity(Intent.createChooser(shareIntent, "Share lesson"))
                }

            } catch (e: IOException) {
                e.printStackTrace()

            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()

        }


    }

    private fun onFootClicked(position: Int) {
        hostSegmentTabControl.onTabHostManager(position + 1)
    }

    private fun onSegmentClicked(position: Int) {
        hostSegmentTabControl.onTabHostManager(position + 1)
    }

    companion object {
        const val EXTRA_SEGMENT = "selected_segment"
        const val EXTRA_CHECKLIST = "selected_checklist"
        const val EXTRA_SEGMENT_TAB_TITLE = "selected_tab_title"
    }

    fun getTitle(): String = titleTab

    fun setIndexTab(position: Int) {
        this.indexTab = position
    }
}