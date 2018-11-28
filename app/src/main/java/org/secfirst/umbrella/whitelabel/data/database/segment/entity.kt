package org.secfirst.umbrella.whitelabel.data.database.segment

import android.os.Parcelable
import com.bluelinelabs.conductor.Controller
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentDetailController
import org.secfirst.umbrella.whitelabel.serialize.PathUtils

@Parcelize
@Table(database = AppDatabase::class,
        allFields = true, useBooleanGetterSetters = false, cachingEnabled = true)
data class Markdown(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @ForeignKey(stubbedRelationship = true)
        var module: Module? = null,
        @ForeignKey(stubbedRelationship = true)
        var subject: Subject? = null,
        @ForeignKey(stubbedRelationship = true)
        var difficulty: Difficulty? = null,
        var text: String = "",
        var title: String = "",
        var index: String = "",
        var favorite: Boolean = false,
        var basePath: String = "") : Parcelable {

    constructor(text: String) : this(0,
            null,
            null,
            null, text, recoveryTitle(text), recoveryIndex(text))

    companion object {
        const val FAVORITE_INDEX = 1L
        private const val TAG_INDEX = "index: "
        private const val TAG_TITLE = "title: "
        const val SINGLE_CHOICE = 1
        const val MARKDOWN_IMAGE_TAG = "![image]("
        fun recoveryIndex(text: String) = text.lines()[1].trim().substringAfterLast(TAG_INDEX)
        fun recoveryTitle(text: String): String {
            var res = text.lines()[2].trim().substringAfterLast(TAG_TITLE)
            if (res.trim() == "_") res = ""
            return res
        }
    }
}

fun Difficulty.toSegmentController(host: Controller): SegmentController {
    val checklist = if (this.checklist.isEmpty()) null else this.checklist.last()
    val controller = SegmentController(this.markdowns, host.applicationContext!!.getString(R.string.lesson_tab), checklist)
    controller.hostSegmentTabControl = host as HostSegmentTabControl
    return controller
}

fun List<Markdown>.toSegmentDetailControllers(): List<SegmentDetailController> {
    val controllers = mutableListOf<SegmentDetailController>()
    val sortedMarkdowns = this.sortedWith(compareBy { it.index })
    sortedMarkdowns.forEach { markdown ->
        val markdowns = mutableListOf<Markdown>()
        markdowns.add(markdown)
        val controller = SegmentDetailController(markdown)
        controllers.add(controller)
    }
    return controllers
}

fun Markdown.removeHead(): Markdown {
    text = text.substringAfterLast(text.lines()[3])
    return this
}

fun String.replaceMarkdownImage(pwd: String) = this.replace(Markdown.MARKDOWN_IMAGE_TAG,
        "${Markdown.MARKDOWN_IMAGE_TAG}file://${PathUtils.basePath()}/$pwd")

inline fun <reified T> MutableList<Markdown>.associateMarkdown(foreignKey: T) {
    this.forEach { mark ->
        when (foreignKey) {
            is Module -> mark.module = foreignKey
            is Subject -> mark.subject = foreignKey
            is Difficulty -> mark.difficulty = foreignKey
        }
    }
}

interface HostSegmentTabControl {
    fun onTabHostManager(position: Int)
}

