package org.secfirst.umbrella.whitelabel.component

import android.app.AlertDialog
import android.view.View
import android.widget.Spinner
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.misc.init

class RefreshIntervalDialog(private val view: View, private val initialPosition: Int,
                            private val listener: RefreshIntervalListener) {

    private val refreshIntervalDialog = AlertDialog
            .Builder(view.context)
            .setView(view)
            .create()

    private lateinit var spinner : Spinner

    init {
        view.alertControlOk.setOnClickListener { refreshIntervalOk() }
        view.alertControlCancel.setOnClickListener { refreshIntervalCancel() }
        prepareRefreshInterval()
    }

    fun show() {
        refreshIntervalDialog.show()
    }

    fun getCurrentChoice() = spinner.selectedItem.toString()

    private fun prepareRefreshInterval() {
        spinner = view.refreshInterval
        spinner.init(R.array.refresh_interval_array)
        spinner.setSelection(initialPosition)
    }

    private fun refreshIntervalOk() {
        val selectedInterval = view.refreshInterval.selectedItem.toString()
        val position = view.refreshInterval.selectedItemPosition
        refreshIntervalDialog.dismiss()
        listener.onRefreshIntervalSuccess(position, selectedInterval)
    }

    private fun refreshIntervalCancel() {
        listener.onRefreshIntervalCancel()
        refreshIntervalDialog.dismiss()
    }

    interface RefreshIntervalListener {
        fun onRefreshIntervalSuccess(selectedPosition: Int, selectedInterval: String)
        fun onRefreshIntervalCancel() {}
    }
}