package org.secfirst.umbrella.models;


import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Category implements Serializable {
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField
    private int parent;
    @DatabaseField
    private String category;
    @SerializedName("has_difficulty")
    @DatabaseField
    private int hasDifficulty;
    @DatabaseField
    @SerializedName("difficulty_beginner")
    private int difficultyBeginner;
    @DatabaseField
    @SerializedName("difficulty_advanced")
    private int difficultyAdvanced;
    @SerializedName("difficulty_expert")
    @DatabaseField
    private int difficultyExpert;
    @DatabaseField
    @SerializedName("text_beginner")
    private String textBeginner;
    @DatabaseField
    @SerializedName("text_advanced")
    private String textAdvanced;
    @DatabaseField
    @SerializedName("text_expert")
    private String textExpert;

    public Category() {}

    public Category(int id, int parent, String category, boolean hasDifficulty, boolean difficultyBeginner, boolean difficultyAdvanced, boolean difficultyExpert, String textBeginner, String textAdvanced, String textExpert) {
        this.id = id;
        this.parent = parent;
        this.category = category;
        this.hasDifficulty = hasDifficulty ? 1 : 0;
        this.difficultyBeginner = difficultyBeginner ? 1 : 0;
        this.difficultyAdvanced = difficultyAdvanced ? 1 : 0;
        this.difficultyExpert = difficultyExpert ? 1 : 0;
        this.textBeginner = textBeginner;
        this.textAdvanced = textAdvanced;
        this.textExpert = textExpert;
    }

    public int getParent() {
        return parent;
    }

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public boolean getDifficultyBeginner() {
        return difficultyBeginner != 0;
    }

    public boolean getDifficultyAdvanced() {
        return difficultyAdvanced != 0;
    }

    public boolean getDifficultyExpert() {
        return difficultyExpert != 0;
    }

    public String getTextExpert() {
        return textExpert;
    }

    public String getTextAdvanced() {
        return textAdvanced;
    }

    public String getTextBeginner() {
        return textBeginner;
    }

    public boolean hasDifficulty() {
        return hasDifficulty != 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Category{");
        sb.append("id='").append(id).append('\'');
        sb.append(",parent='").append(parent).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", hasDifficulty='").append(hasDifficulty).append('\'');
        sb.append(", difficultyBeginner='").append(difficultyBeginner).append('\'');
        sb.append(", difficultyAdvanced='").append(difficultyAdvanced).append('\'');
        sb.append(", difficultyExpert='").append(difficultyExpert).append('\'');
        sb.append(", textAdvanced='").append(textAdvanced).append('\'');
        sb.append(", textExpert='").append(textExpert).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
