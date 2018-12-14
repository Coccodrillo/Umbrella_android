package org.secfirst.umbrella.whitelabel.data.database.checklist

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.ChecklistController


@Parcelize
@Table(database = AppDatabase::class, useBooleanGetterSetters = false, cachingEnabled = true)
data class Checklist(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @Column
        var index: Int = 0,

        @Column
        var progress: Int = 0,

        @Column
        var favorite: Boolean = false,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "category_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var module: Module? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "subcategory_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var subject: Subject? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var difficulty: Difficulty? = null,

        @JsonProperty("list")
        var content: MutableList<Content> = arrayListOf()) : Parcelable {


    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "content")
    fun oneToManyContent(): MutableList<Content> {
        if (content.isEmpty()) {
            content = SQLite.select()
                    .from(Content::class.java)
                    .where(Content_Table.checklist_id.eq(id))
                    .queryList()
        }
        return content
    }
}

fun List<Checklist>.toChecklistControllers(): List<ChecklistController> {
    val controllers = mutableListOf<ChecklistController>()
    this.forEach { checklist ->
        val checklists = mutableListOf<Checklist>()
        checklists.add(checklist)
        val controller = ChecklistController(checklist)
        controllers.add(controller)
    }
    return controllers
}

@Parcelize
@Table(database = AppDatabase::class, useBooleanGetterSetters = false)
class Content(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var check: String = "",
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "checklist_id")
        var checklist: Checklist? = null,
        @Column
        var label: String = "",
        @Column
        var value: Boolean = false) : BaseModel(), Parcelable

class Dashboard(var items: List<Item> = listOf()) {

    data class Item(var id: Long = 0,
                    var progress: Int = 0,
                    var title: String = "",
                    var label: String = "",
                    var checklist: Checklist? = null,
                    var difficulty: Difficulty?) {

        constructor(progress: Int, label: String,
                    checklist: Checklist?,
                    difficulty: Difficulty?) : this(0, progress, "", label, checklist, difficulty)

        constructor(title: String) : this(0, 0, title, "", null, null)
    }
}

fun Checklist.covertToHTML(): String {
    var body = "<html><head><meta Content-Type: text/html; charset=\"UTF-8\"></head><body style=\"font-family: DejaVu Sans; font-size:16px; font-weight: normal;\" >"
    this.content.forEach { content ->
        body += (if (content.value) "✓" else "✗") + " ${content.check}"
        body += "<br>"
    }
    body += "</body></html>"
    return body
}

inline fun <reified T> MutableList<Checklist>.associateChecklist(foreignKey: T) {
    this.forEach { checklist ->
        when (foreignKey) {
            is Module -> checklist.module = foreignKey
            is Subject -> checklist.subject = foreignKey
            is Difficulty -> checklist.difficulty = foreignKey
        }
        checklist.content.forEach { content ->
            content.checklist = checklist
        }
    }
}


