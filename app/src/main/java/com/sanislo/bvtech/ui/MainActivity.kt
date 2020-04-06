package com.sanislo.bvtech.ui

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.util.Log.d
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sanislo.bvtech.R
import com.sanislo.bvtech.domain.EventObserver
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val feedAdapter = FeedListAdapter()
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv_feed.adapter = feedAdapter
        (rv_feed.layoutManager as LinearLayoutManager).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        initSearchInputListener()
        observeFeedList()
        observeErrors()
        observeNetworkState()
    }

    private fun observeErrors() {
        viewModel.error.observe(this, EventObserver {
            Snackbar.make(root, it.message ?: getString(R.string.unknown_error), Snackbar.LENGTH_LONG).show()
        })
    }

    private fun observeFeedList() {
        viewModel.feedListList.observe(this, Observer {
            d(TAG, "observeFeedList ${it.size}")
            feedAdapter.submitList(it)
        })
    }

    private fun observeNetworkState() {
        viewModel.networkState.observe(this, Observer { isOnline ->
            if (isOnline) {
                snackbar?.dismiss()
            } else {
                snackbar = Snackbar.make(root, R.string.offline, Snackbar.LENGTH_INDEFINITE)
                snackbar?.show()
            }
        })
    }

    private fun initSearchInputListener() {
        input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = input.text.toString()
        dismissKeyboard(v.windowToken)
        viewModel.setQuery(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, input.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.setQuery(savedInstanceState.getString(KEY_QUERY, null))
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        d(TAG, "onDestroy")
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        const val KEY_QUERY = "KEY_QUERY"
    }
}
