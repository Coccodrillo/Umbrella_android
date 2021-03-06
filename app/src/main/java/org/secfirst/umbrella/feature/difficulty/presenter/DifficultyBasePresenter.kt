package org.secfirst.umbrella.feature.difficulty.presenter

import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.feature.difficulty.view.DifficultyView

interface DifficultyBasePresenter<V : DifficultyView, I : DifficultyBaseInteractor> : BasePresenter<V, I> {

    fun submitDifficulty(subjectId: String)

    fun saveDifficultySelect(difficulty: Difficulty, subjectId: String)

    fun submitDifficultySelect(difficulties: List<Difficulty>)
}